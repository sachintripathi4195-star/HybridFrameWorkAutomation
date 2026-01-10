package helperMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.openqa.selenium.Dimension; // added for headless window sizing

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ExtentTestManager;
import utils.Log;


/**
 * GenricMethod provides common WebDriver helper methods used across tests.
 *
 * <p>It contains browser setup/teardown helpers, element interaction wrappers
 * (click, send keys, waits), utilities for alerts, frames, windows and
 * screenshot capturing. Methods include defensive fallbacks (JS click,
 * Actions) and centralized error handling that logs to the project's
 * extent reports and saves screenshots when operations fail.</p>
 *
 * Note: This class exposes a public static WebDriver (`driver`) to be shared
 * across tests. Ensure tests manage driver lifecycle appropriately.
 */
public class GenricMethod {

    /** The shared WebDriver instance used by helper methods. */
    public static WebDriver driver;

    /** Properties loaded from src/test/resources/Application.properties. */
    public static Properties prop;

    /** SoftAssert instance for deferred assertions in helper methods if needed. */
    SoftAssert soft = new SoftAssert();

    ////////////////////////////////////Read Property File ////////////////////////////////////////

    static {
        try {

            FileInputStream file = new FileInputStream(
                    System.getProperty("user.dir") + "/src/test/resources/Application.properties");

            prop = new Properties();
            prop.load(file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //////////////////////////////////Using Genric Explicit wait//////////////////////////////////

    /**
     * Launches the application based on browser setting in properties.
     * Supported browsers: chrome, firefox, edge. Browser name is read from
     * Application.properties -> Browser and url from url property.
     *
     * This method sets a default implicit wait and maximizes the window.
     * It will throw a RuntimeException for an unsupported browser name.
     */
    public void launchApplication() {

        String browserName = prop.getProperty("Browser").trim().toLowerCase();
        String appUrl = prop.getProperty("url").trim();

        // Read headless setting (default false)
        String headlessProp = prop.getProperty("Headless", "false").trim().toLowerCase();
        boolean headless = headlessProp.equals("true") || headlessProp.equals("yes");

        switch (browserName) {

        case "chrome":
            WebDriverManager.chromedriver().setup();
            ChromeOptions chromeOpt = new ChromeOptions();
            chromeOpt.addArguments("--disable-incognito");
            // Apply headless only when requested. Using setHeadless keeps compatibility
            if (headless) {
                // Use headless arguments compatible with multiple ChromeDriver/Selenium versions
                chromeOpt.addArguments("--headless=new");
                chromeOpt.addArguments("--headless");

                // Ensure a fixed window size when running headless so elements that depend on layout
                // are present and visible.
                chromeOpt.addArguments("--window-size=1920,1080");

                // Helpful flags for running in CI/headless environments
                chromeOpt.addArguments("--no-sandbox");
                chromeOpt.addArguments("--disable-gpu");
                chromeOpt.addArguments("--disable-dev-shm-usage");
                chromeOpt.addArguments("--remote-debugging-port=9222");
                chromeOpt.addArguments("--disable-software-rasterizer");
                // allow remote origins in case of ChromeDriver v111+ requiring this
                chromeOpt.addArguments("--remote-allow-origins=*");
            } else {
                // Non-headless recommended flags
                chromeOpt.addArguments("--remote-allow-origins=*");
                chromeOpt.addArguments("--no-sandbox");
                chromeOpt.addArguments("--disable-dev-shm-usage");
            }

            driver = new ChromeDriver(chromeOpt);

            // If headless, explicitly set the window size; otherwise maximize window
            if (headless) {
                try {
                    driver.manage().window().setSize(new Dimension(1920, 1080));
                } catch (Exception e) {
                    Log.warn("Unable to explicitly set window size in headless mode: " + e.getMessage());
                }
            } else {
                try {
                    driver.manage().window().maximize();
                } catch (Exception e) {
                    Log.warn("Unable to maximize window: " + e.getMessage());
                }
            }
            break;

        case "firefox":
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions ffOpt = new FirefoxOptions();
            ffOpt.addPreference("browser.privatebrowsing.autostart", false);
            driver = new FirefoxDriver(ffOpt);
            break;

        case "edge":
            WebDriverManager.edgedriver().setup();
            EdgeOptions edgeOpt = new EdgeOptions();
            edgeOpt.addArguments("--disable-inprivate");
            driver = new EdgeDriver(edgeOpt);
            break;

        default:
            throw new RuntimeException("Invalid Browser Name in properties file: " + browserName);
        }

        // Use a reasonable implicit wait. Explicit waits are preferred for specific elements.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(90)); // Increased timeout
        // Add a page load timeout to avoid hanging on heavy pages in headless mode
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
        } catch (Exception e) {
            Log.warn("pageLoadTimeout not supported by this driver: " + e.getMessage());
        }
        driver.get(appUrl);
        // Wait for the page to finish loading; useful to avoid timeouts in headless
        try {
            waitForPageToLoad();
            // short pause to allow rendering of dynamic parts
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    ///////////////////////////////Using Genric Explicit wait//////////////////////////////////

    /**
     * Waits for visibility of an element located by the given locator and
     * returns the found WebElement. Uses explicit wait with 20 seconds timeout.
     *
     * @param by locator used to find the element
     * @return visible WebElement
     * @throws org.openqa.selenium.TimeoutException if element not visible within timeout
     */
    public static WebElement waitForExpectedElement(By by) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));

    }

    /**
     * Waits for the browser page's document.readyState to become 'complete'.
     * This helps ensure scripts and resources are loaded before interactions,
     * which is especially helpful in headless runs where timing differs.
     */
    public static void waitForPageToLoad() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            Log.warn("waitForPageToLoad encountered an issue: " + e.getMessage());
        }
    }

    //////////////////////////Handling Exception//////////////////////////////////////////

    /**
     * Centralized exception handling used by other helper methods. Logs the
     * error, captures screenshot and attaches the screenshot to the current
     * Extent report test (if available).
     *
     * @param e the caught exception
     * @param locator locator related to the action that failed (for logging)
     * @param action descriptive action name used in screenshot filename and logs
     */
    private static void handleException(Exception e, By locator, String action) {

        String screenshotPath = takeScreenShot(action + "_" + locator);

        Log.error("ACTION FAILED : " + action);
        Log.error("LOCATOR       : " + locator);
        Log.error("EXCEPTION     : " + e.getMessage());

        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest()
                    .fail("Failure Screenshot")
                    .addScreenCaptureFromPath(screenshotPath);
        }
    }



    ////////////////////////Clicking Element////////////////////////////////////////

  

    /**
     * Attempts to click on an element located by the given locator. This method
     * uses a sequence of fallbacks: normal click -> JavaScript click -> Actions
     * click. On failure the exception handler is invoked and the test is failed
     * using TestNG Assert.fail.
     *
     * @param by locator of the element to click
     */
    public void clickOnElement(By by) {

        WebElement ele = null;

        try {
            ele = waitForExpectedElement(by);
        } catch (Exception e) {
            handleException(e, by, "WAIT_FOR_ELEMENT");
            Assert.fail("Element not found : " + by);
            return;
        }


        try {
            ele.click();
            Log.info("Clicked (Normal) on element : " + by);
            return;
        } catch (Exception e) {
            Log.warn("Normal click failed on : " + by + " | Trying JS click");
        }


        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", ele);
            Log.info("Clicked (JavaScript) on element : " + by);
            return;
        } catch (Exception e) {
            Log.warn("JS click failed on : " + by + " | Trying Actions click");
        }


        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(ele).click().perform();
            Log.info("Clicked (Actions) on element : " + by);
        } catch (Exception e) {
            handleException(e, by, "CLICK");
            Assert.fail("Unable to click element : " + by);
        }
    }



/////////////////////////////////////////Sendkeys Method/////////////////////////////////////////////////////

    /**
     * Clears any existing text and enters the provided value into the web
     * element located by `by`. This method attempts multiple strategies in
     * order: sendKeys, JavaScript assignment, and Actions-based typing.
     *
     * @param by locator of the input element
     * @param value text value to enter
     */
    public void clearAndEnterText(By by, String value) {

        WebElement ele = null;

        try {
            ele = waitForExpectedElement(by);
            ele.clear();
            ele.sendKeys(value);

            Log.info("Text entered using sendKeys on : " + by);
            return;

        } catch (Exception e1) {
            Log.warn("sendKeys failed, trying JavaScript for : " + by);
        }


        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].value='';", ele);
            js.executeScript("arguments[0].value=arguments[1];", ele, value);

            Log.info("Text entered using JavaScript on : " + by);
            return;

        } catch (Exception e2) {
            Log.warn("JavaScript failed, trying Actions for : " + by);
        }


        try {
            Actions actions = new Actions(driver);
            actions.click(ele)
                   .keyDown(Keys.CONTROL)
                   .sendKeys("a")
                   .keyUp(Keys.CONTROL)
                   .sendKeys(value)
                   .build()
                   .perform();

            Log.info("Text entered using Actions on : " + by);
            return;

        } catch (Exception e3) {
            handleException(e3, by, "ClearAndEnterText");
        }
    }

///////////////////////////////GetData For TextComparision/////////////////////////////////////////////

    /**
     * Retrieves the visible text from the element located by `by` and compares
     * it to the expected value. Logs success on match, otherwise logs details
     * and calls the centralized exception handler.
     *
     * @param by locator of the element
     * @param expectedValue expected text to compare against
     */
    public void getDataWithTextComparison(By by, String expectedValue) {

        try {
            WebElement ele = waitForExpectedElement(by);
            String actualValue = ele.getText().trim();

            if (actualValue.equals(expectedValue)) {
                Log.info("Text matched successfully : " + expectedValue);
            } else {
                Log.error("Text mismatch!");
                Log.error("Expected : " + expectedValue);
                Log.error("Actual   : " + actualValue);

                handleException(
                    new Exception("TextComparison Failed"),
                    by,
                    "TextComparison"
                );
            }

        } catch (Exception e) {
            handleException(e, by, "GetDataWithTextComparison");
        }
    }

///////////////////////////////Get Text Normal//////////////////////////////////////
    /**
     * NOTE: This method uses a passed-in parameter `value` and assigns the
     * element text to it. Because Java is pass-by-value for references, this
     * DOES NOT change the caller's variable. Prefer using
     * `getTextnormalWithretun(By)` which returns the text.
     *
     * Kept for backward compatibility with callers expecting this signature,
     * but it's recommended to migrate callers to the returned-value variant.
     *
     * @param by locator of the element
     * @param value a String parameter that will receive the text inside this
     *              method only (caller will not see changes)
     */
    public void getTextNormal(By by, String value) {

        WebElement ele = waitForExpectedElement(by);

        value = ele.getText();

    }

    
    //////////////////Get Text From Normal Return One/////////////////////////////////
    /**
     * Returns the visible text of the element located by `by`.
     *
     * @param by locator of the element
     * @return visible text of the element
     */
    public String getTextnormalWithretun(By by) {

        WebElement ele = waitForExpectedElement(by);

        return ele.getText();

    }

    
    ////////////////////Read Data From List////////////////////////////////
    /**
     * Finds elements using the provided locator, iterates over them and tries
     * to find one whose text equals the expected value. If not found, it
     * triggers the centralized exception handler so the failure is recorded.
     *
     * @param list unused input list; a fresh list is created by locating elements by `by`
     * @param by locator used to find elements
     * @param expectedValue the value to search for in the list of elements
     */
    public void ReadDataFromList(List<WebElement> list, By by, String expectedValue) {

        try {
            list = driver.findElements(by);

            boolean isFound = false;

            for (WebElement ele : list) {

                String value = ele.getText().trim();

                if (value.equals(expectedValue)) {
                    Log.info("Expected Value Is Displayed = " + expectedValue);
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                handleException(
                    new Exception("Expected value not found in list : " + expectedValue),
                    by,
                    "ReadDataFromList"
                );
            }

        } catch (Exception e) {
            handleException(e, by, "ReadDataFromList");
        }
    }

        
        
        
    ///////////////////Read Row And Coloumn /////////////////////////////////////////////
    /**
     * Reads either rows or columns from a table represented by the provided
     * locator and stores the trimmed text into an ArrayList. The collected
     * values are not returned; modify this method to return the list if
     * consumers need the data programmatically.
     *
     * @param list unused input list; replaced by elements found by `by`
     * @param by locator used to find table cells
     * @param SideName either "Row" or "coloumn" to control how collection is described
     */
    public void ReadTableRowAndColoumn(List<WebElement> list, By by, String SideName) {

        list = driver.findElements(by);

        ArrayList<String> a = new ArrayList<>();

        switch (SideName) {

        case "Row":

            for (int i = 0; i < list.size(); i++) {

                String value = list.get(i).getText().trim();

                a.add(value);

            }
            break;
        case "coloumn":

            for (int i = 0; i < list.size(); i++) {

                String value = list.get(i).getText().trim();
                a.add(value);

            }
            break;

        }

    }

    
    //////////////////////////////////////////Alert Method's/////////////////////////////////////////////
    /**
     * Handles JavaScript alerts by type: accept, dismiss or get text.
     *
     * @param Type one of: "accept", "dismiss", "text" (case-insensitive)
     */
    
    public void AlertMethod(String Type) {

        Alert a = driver.switchTo().alert();

        switch (Type.toLowerCase()) {

        case "accept":
            a.accept();
            break;

        case "text":
            a.getText();
            break;

        case "dismiss":
            a.dismiss();
            break;

        }

    }

    //////////////////////////////////Swithcing frmae//////////////////////////
    /**
     * Switches driver's context to a frame by id or name.
     *
     * @param id frame id or name
     */
    public void switchFrame(String id) {

        driver.switchTo().frame(id);

    }

     //////////////////////Take Screen shot///////////////////////////////////////
    /**
     * Captures a screenshot and saves it to a ScreenShot directory under
     * project root. Returns the saved file path.
     *
     * @param ssName base name used for screenshot file (special chars are sanitized)
     * @return absolute path to the saved screenshot
     */
    public static String takeScreenShot(String ssName) {

        String projectPath = System.getProperty("user.dir");  
        String folderPath = projectPath + File.separator + "ScreenShot";

        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();   
        }

        
        String safeName = ssName.replaceAll("[^a-zA-Z0-9]", "_");
        String path = folderPath + File.separator + safeName + ".png";

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            FileHandler.copy(src, new File(path));
            Log.info("Screenshot saved at: " + path);
        } catch (Exception e) {
            Log.error("Screenshot capture failed: " + e.getMessage());
        }

        return path;   
    }

    /////////////////////////////////////////SwitchWindow////////////////////////////////////////    

    /**
     * Switches to a browser window whose title exactly matches the provided
     * title. Iterates all open windows and selects the first matching one.
     *
     * @param title exact title string to match
     */
    public void switchToWindow(String title) {

        Set<String> allwindows = driver.getWindowHandles();

        for (String window : allwindows) {

            driver.switchTo().window(window);

            String ActualTitle = driver.getTitle();

            if (ActualTitle.equals(title)) {

                System.out.println("Title Has been Equals To the Expected Title");
                break;
            }

        }

    }




	///////////////////////////////////////////Select DropDown By Value, Index, VisibleText//////////////////////////////////////
    
    /**
	 * Selects an option from a dropdown located by `by` based on the method
	 * specified in Application.properties under "SelectValue":
	 * - "byValue": selects by option value attribute
	 * - "byIndex": selects by option index (0-based)
	 * - "byVisibleText": selects by visible text
	 *
	 * The corresponding value/index/text is read from properties:
	 * - "Value" for byValue
	 * - "Index" for byIndex
	 * - "VisibleText" for byVisibleText
	 *
	 * @param by locator of the select element
	 */    
    
    public static void selectDropdonwnByValue(By by,String type,String value) {
    
    	Select s = new Select(waitForExpectedElement(by));
    
    	switch (type.trim().toLowerCase()) {
    
    	case "byValue":
		 s.selectByValue(value.trim());
		 break;
    	case "byIndex":
			s.selectByIndex(Integer.parseInt(value));
			break;
    	case "byVisibleText":
			s.selectByVisibleText(value.trim());
			break;
		}
    
    
    }
    
    
    public static void ReadAndClickDataFromList(By by,String ExpectedMasterOptions) {
     
    	List<WebElement> list = driver.findElements(by);
     
    	for (WebElement ele : list) {
			
			String value = ele.getText().trim();
			
			if (value.equalsIgnoreCase(ExpectedMasterOptions)) {
				
				ele.click();
				
				Log.info("Clicked on Master Data Option From Dashboard Page");
				
				break;
			}
			
		}
     	
    }
    
    
}

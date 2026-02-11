package helperMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
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
 * <p>
 * It contains browser setup/teardown helpers, element interaction wrappers
 * (click, send keys, waits), utilities for alerts, frames, windows and
 * screenshot capturing. Methods include defensive fallbacks (JS click, Actions)
 * and centralized error handling that logs to the project's extent reports and
 * saves screenshots when operations fail.
 * </p>
 *
 * Note: This class exposes a public static WebDriver (`driver`) to be shared
 * across tests. Ensure tests manage driver lifecycle appropriately.
 */
public class GenricMethod {

	/** The shared WebDriver instance used by helper methods. */
	public static WebDriver driver;

	/** Properties loaded from src/test/resources/Application.properties. */
	public static Properties prop;

	public static Properties process;

	/** SoftAssert instance for deferred assertions in helper methods if needed. */
	SoftAssert soft = new SoftAssert();

	//////////////////////////////////// Read Property File
	//////////////////////////////////// ////////////////////////////////////////

	static {
		try {

			FileInputStream file = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/Application.properties");

			prop = new Properties();
			prop.load(file);

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			FileInputStream file = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/process.properties");

			process = new Properties();
			process.load(file);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	////////////////////////////////// Using Genric Explicit
	////////////////////////////////// wait//////////////////////////////////

	/**
	 * Launches the application based on browser setting in properties. Supported
	 * browsers: chrome, firefox, edge. Browser name is read from
	 * Application.properties -> Browser and url from url property.
	 *
	 * This method sets a default implicit wait and maximizes the window. It will
	 * throw a RuntimeException for an unsupported browser name.
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
				// Use headless arguments compatible with multiple ChromeDriver/Selenium
				// versions
				chromeOpt.addArguments("--headless=new");
				chromeOpt.addArguments("--headless");

				// Ensure a fixed window size when running headless so elements that depend on
				// layout
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

		// Use a reasonable implicit wait. Explicit waits are preferred for specific
		// elements.
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

	/////////////////////////////// Using Genric Explicit
	/////////////////////////////// wait//////////////////////////////////

	/**
	 * Waits for visibility of an element located by the given locator and returns
	 * the found WebElement. Uses explicit wait with 20 seconds timeout.
	 *
	 * @param by locator used to find the element
	 * @return visible WebElement
	 * @throws org.openqa.selenium.TimeoutException if element not visible within
	 *                                              timeout
	 */
	public static WebElement waitForExpectedElement(By by) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(by));

	}

	/**
	 * Waits for the browser page's document.readyState to become 'complete'. This
	 * helps ensure scripts and resources are loaded before interactions, which is
	 * especially helpful in headless runs where timing differs.
	 */
	public static void waitForPageToLoad() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
			wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
					.equals("complete"));
		} catch (Exception e) {
			Log.warn("waitForPageToLoad encountered an issue: " + e.getMessage());
		}
	}

	////////////////////////// Handling
	////////////////////////// Exception//////////////////////////////////////////

	/**
	 * Centralized exception handling used by other helper methods. Logs the error,
	 * captures screenshot and attaches the screenshot to the current Extent report
	 * test (if available).
	 *
	 * @param e       the caught exception
	 * @param locator locator related to the action that failed (for logging)
	 * @param action  descriptive action name used in screenshot filename and logs
	 */
	private static void handleException(Exception e, By locator, String action) {

		String screenshotPath = takeScreenShot(action + "_" + locator);

		Log.error("ACTION FAILED : " + action);
		Log.error("LOCATOR       : " + locator);
		Log.error("EXCEPTION     : " + e.getMessage());

		if (ExtentTestManager.getTest() != null) {
			ExtentTestManager.getTest().fail("Failure Screenshot").addScreenCaptureFromPath(screenshotPath);
		}
	}

	//////////////////////// Clicking
	//////////////////////// Element////////////////////////////////////////

	/**
	 * Attempts to click on an element located by the given locator. This method
	 * uses a sequence of fallbacks: normal click -> JavaScript click -> Actions
	 * click. On failure the exception handler is invoked and the test is failed
	 * using TestNG Assert.fail.
	 *
	 * @param by locator of the element to click
	 */
	public static void clickOnElement(By by) {

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
	 * Clears any existing text and enters the provided value into the web element
	 * located by `by`. This method attempts multiple strategies in order: sendKeys,
	 * JavaScript assignment, and Actions-based typing.
	 *
	 * @param by    locator of the input element
	 * @param value text value to enter
	 */
	public static void clearAndEnterText(By by, String value) {

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
			actions.click(ele).keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(value).build()
					.perform();

			Log.info("Text entered using Actions on : " + by);
			return;

		} catch (Exception e3) {
			handleException(e3, by, "ClearAndEnterText");
		}
	}

	/**
	 * Overloaded helper: Clears and enters text into an already-located WebElement.
	 * Mirrors the logic of the By-based method so callers that pass WebElement
	 * (e.g., clearAndEnterText(waitForExpectedElement(...), value)) continue to work.
	 *
	 * @param ele   already-located WebElement
	 * @param value text to enter
	 */
	public void clearAndEnterText(WebElement ele, String value) {

		By dummyBy = By.tagName("input"); // used only for error logging context

		try {
			ele.clear();
			ele.sendKeys(value);
			Log.info("Text entered using sendKeys on WebElement");
			return;

		} catch (Exception e1) {
			Log.warn("sendKeys on WebElement failed, trying JavaScript");
		}

		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].value='';", ele);
			js.executeScript("arguments[0].value=arguments[1];", ele, value);
			Log.info("Text entered using JavaScript on WebElement");
			return;
		} catch (Exception e2) {
			Log.warn("JavaScript entry on WebElement failed, trying Actions");
		}

		try {
			Actions actions = new Actions(driver);
			actions.click(ele).keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(value).build()
					.perform();
			Log.info("Text entered using Actions on WebElement");
			return;
		} catch (Exception e3) {
			handleException(e3, dummyBy, "ClearAndEnterText(WebElement)");
		}
	}

///////////////////////////////GetData For TextComparision/////////////////////////////////////////////

	/**
	 * Retrieves the visible text from the element located by `by` and compares it
	 * to the expected value. Logs success on match, otherwise logs details and
	 * calls the centralized exception handler.
	 *
	 * @param by            locator of the element
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

				handleException(new Exception("TextComparison Failed"), by, "TextComparison");
			}

		} catch (Exception e) {
			handleException(e, by, "GetDataWithTextComparison");
		}
	}

///////////////////////////////Get Text Normal//////////////////////////////////////
	/**
	 * NOTE: This method uses a passed-in parameter `value` and assigns the element
	 * text to it. Because Java is pass-by-value for references, this DOES NOT
	 * change the caller's variable. Prefer using `getTextnormalWithretun(By)` which
	 * returns the text.
	 *
	 * Kept for backward compatibility with callers expecting this signature, but
	 * it's recommended to migrate callers to the returned-value variant.
	 *
	 * @param by    locator of the element
	 * @param value a String parameter that will receive the text inside this method
	 *              only (caller will not see changes)
	 */
	public void getTextNormal(By by, String value) {

		WebElement ele = waitForExpectedElement(by);

		value = ele.getText();

	}

	////////////////// Get Text From Normal Return
	////////////////// One/////////////////////////////////
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

	//////////////////// Read Data From List////////////////////////////////
	/**
	 * Finds elements using the provided locator, iterates over them and tries to
	 * find one whose text equals the expected value. If not found, it triggers the
	 * centralized exception handler so the failure is recorded.
	 *
	 * @param list          unused input list; a fresh list is created by locating
	 *                      elements by `by`
	 * @param by            locator used to find elements
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
				handleException(new Exception("Expected value not found in list : " + expectedValue), by,
						"ReadDataFromList");
			}

		} catch (Exception e) {
			handleException(e, by, "ReadDataFromList");
		}
	}

	/////////////////// Read Row And Coloumn
	/////////////////// /////////////////////////////////////////////
	/**
	 * Reads either rows or columns from a table represented by the provided locator
	 * and stores the trimmed text into an ArrayList. The collected values are not
	 * returned; modify this method to return the list if consumers need the data
	 * programmatically.
	 *
	 * @param list     unused input list; replaced by elements found by `by`
	 * @param by       locator used to find table cells
	 * @param SideName either "Row" or "coloumn" to control how collection is
	 *                 described
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

	////////////////////////////////////////// Alert
	////////////////////////////////////////// Method's/////////////////////////////////////////////
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

	////////////////////////////////// Swithcing frmae//////////////////////////
	/**
	 * Switches driver's context to a frame by id or name.
	 *
	 * @param id frame id or name
	 */
	public void switchFrame(String id) {

		driver.switchTo().frame(id);

	}

	////////////////////// Take Screen shot///////////////////////////////////////
	/**
	 * Captures a screenshot and saves it to a ScreenShot directory under project
	 * root. Returns the saved file path.
	 *
	 * @param ssName base name used for screenshot file (special chars are
	 *               sanitized)
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

	///////////////////////////////////////// SwitchWindow////////////////////////////////////////

	/**
	 * Switches to a browser window whose title exactly matches the provided title.
	 * Iterates all open windows and selects the first matching one.
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

//give me command for terminal where can convert to main project dir     

	/////////////////////////////////////////// Select DropDown By Value, Index,
	/////////////////////////////////////////// VisibleText//////////////////////////////////////

	/**
	 * Selects an option from a dropdown located by `by` based on the method
	 * specified in Application.properties under "SelectValue": - "byValue": selects
	 * by option value attribute - "byIndex": selects by option index (0-based) -
	 * "byVisibleText": selects by visible text
	 *
	 * The corresponding value/index/text is read from properties: - "Value" for
	 * byValue - "Index" for byIndex - "VisibleText" for byVisibleText
	 *
	 * @param by locator of the select element
	 */

	public static void selectDropdonwnByValue(By by, String arg1, String arg2) {

        WebElement ele = waitForExpectedElement(by);

        // Normalize inputs
        String a1 = arg1 == null ? "" : arg1.trim();
        String a2 = arg2 == null ? "" : arg2.trim();
        String a1l = a1.toLowerCase();
        String a2l = a2.toLowerCase();

        // Recognized type tokens
        List<String> typeTokens = Arrays.asList("byvalue", "byindex", "byvisibletext", "byvisible", "value", "index", "visibletext", "visible", "text", "val", "idx");

        String type = null;
        String value = null;

        if (typeTokens.contains(a1l) && !typeTokens.contains(a2l)) {
            type = a1l;
            value = a2;
        } else if (typeTokens.contains(a2l) && !typeTokens.contains(a1l)) {
            type = a2l;
            value = a1;
        } else {
            // Neither arg clearly a type token (or both are). Infer:
            // if one looks numeric -> index
            try {
                Integer.parseInt(a1);
                type = "index";
                value = a1;
            } catch (NumberFormatException nfe1) {
                try {
                    Integer.parseInt(a2);
                    type = "index";
                    value = a2;
                } catch (NumberFormatException nfe2) {
                    // default to visible text; prefer a1 as visible text
                    type = "visibletext";
                    value = a1.isEmpty() ? a2 : a1;
                }
            }
        }

        // normalize type names
        if (type.equals("visible")) type = "visibletext";
        if (type.equals("text")) type = "visibletext";
        if (type.equals("byvisible")) type = "visibletext";
        if (type.equals("byvisibletext")) type = "visibletext";
        if (type.equals("byvalue")) type = "value";
        if (type.equals("byindex")) type = "index";
        if (type.equals("val")) type = "value";
        if (type.equals("idx")) type = "index";

        // Try native Select when possible
        try {
            Select s = new Select(ele);
            switch (type) {
                case "value":
                    s.selectByValue(value.trim());
                    return;
                case "index":
                    s.selectByIndex(Integer.parseInt(value.trim()));
                    return;
                case "visibletext":
                default:
                    s.selectByVisibleText(value.trim());
                    return;
            }
        } catch (Exception e) {
            Log.warn("Standard Select usage failed for " + by + "; falling back to JS/DOM selection: " + e.getMessage());
        }

        // Fallback JS selection by option text (handles select2/hidden selects)
        try {
            String script = "var sel = arguments[0]; for(var i=0;i<sel.options.length;i++){ if(sel.options[i].text.trim()==arguments[1].trim()){ sel.selectedIndex = i; sel.dispatchEvent(new Event('change')); return true; } } return false;";
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object result = js.executeScript(script, ele, value);
            if (result instanceof Boolean && ((Boolean) result)) {
                Log.info("selectDropdonwnByValue: Selected option via JS fallback for value/text '" + value + "'");
                return;
            }
        } catch (Exception ex) {
            Log.warn("JS fallback for select failed: " + ex.getMessage());
        }

        // Last resort: click visible option elements in DOM (if any)
        try {
            List<WebElement> options = ele.findElements(By.tagName("option"));
            for (WebElement opt : options) {
                if (opt.getText() != null && opt.getText().trim().equalsIgnoreCase(value.trim())) {
                    try { opt.click(); Log.info("Clicked option element directly"); return; } catch (Exception e) { /* ignore */ }
                }
            }
        } catch (Exception e) {
            Log.warn("Direct option click fallback failed: " + e.getMessage());
        }

        // If reached, fail with helpful message
        handleException(new Exception("Unable to select dropdown value/text: " + value + " (inferred type=" + type + ")"), by, "SelectDropDown");
    }

	public static void ReadAndClickDataFromList(By by, String ExpectedMasterOptions) {

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
	
	public static void deleteFile(File file) {
		if (file != null && file.exists()) {
			if (file.delete()) {
				System.out.println("File deleted successfully: " + file.getName());

			} else {
				System.out.println("File Deleted Successfully" + file.getName());
			}

		}
	}
	public static File getLatestFile() {
		String downloadPath = System.getProperty("user.dir") + "\\downloads";
		File dir = new File(downloadPath);

		File[] files = dir.listFiles((dir1, name) -> name.contains("Process List") && name.endsWith(".xlsx"));

		if (files == null || files.length == 0) {
		return null;
		}

		// Sort files by last modified date (newest first)
		File latestFile = files[0];
		for (File file : files) {
		if (file.lastModified() > latestFile.lastModified()) {
		latestFile = file;
		}
		}
		return latestFile;
		}
	public static File getLatestFileProcessMasterForSupplier() {
	    String downloadPath = System.getProperty("user.dir") + File.separator + "downloads";
	    File dir = new File(downloadPath);

	    // Ensure folder exists
	    if (!dir.exists()) {
	        dir.mkdirs();
	        System.out.println(" Downloads directory was missing — created: " + dir.getAbsolutePath());
	    }

	    // Filter only matching Excel files
	    File[] files = dir.listFiles((d, name) -> name.contains("Process Master_") && name.toLowerCase().endsWith(".xlsx"));

	    if (files == null || files.length == 0) {
	        System.out.println(" No 'Process Master' Excel files found in: " + downloadPath);
	        return null;
	    }

	    // Find the most recently modified file
	    File latestFile = Arrays.stream(files)
	            .max(Comparator.comparingLong(File::lastModified))
	            .orElse(null);

	    if (latestFile != null) {
	        System.out.println(" Latest 'Process Master' Excel file found: " + latestFile.getName());
	        System.out.println(" Last Modified: " + new Date(latestFile.lastModified()));
	    } else {
	        System.out.println(" No valid 'Process Master' file found after filtering.");
	    }

	    return latestFile;
	}


}
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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ExtentTestManager;
import utils.Log;


public class GenricMethod {

	public static WebDriver driver;
	public static Properties prop;
	
	SoftAssert soft = new SoftAssert();

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
	
	
	
	
	public void launchApplication() {

		String browserName = prop.getProperty("Browser").trim().toLowerCase();
		String appUrl = prop.getProperty("url").trim();

		switch (browserName) {

		case "chrome":
			WebDriverManager.chromedriver().setup();
			ChromeOptions chromeOpt = new ChromeOptions();
			chromeOpt.addArguments("--disable-incognito");
			chromeOpt.addArguments("--disable-headless");
			driver = new ChromeDriver(chromeOpt);
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

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		driver.get(appUrl);
	}
	
	
	public WebElement waitForExpectedElement(By by) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(by));

	}
	
	
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



	public void clickOnElement(By by) {

	    WebElement ele = null;

	    try {
	        ele = waitForExpectedElement(by);
	    } catch (Exception e) {
	        handleException(e, by, "WAIT_FOR_ELEMENT");
	        Assert.fail("Element not found : " + by);
	        return;
	    }

	    // 1️⃣ Normal Selenium click
	    try {
	        ele.click();
	        Log.info("Clicked (Normal) on element : " + by);
	        return;
	    } catch (Exception e) {
	        Log.warn("Normal click failed on : " + by + " | Trying JS click");
	    }

	    // 2️⃣ JavaScript click (fallback)
	    try {
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	        js.executeScript("arguments[0].click();", ele);
	        Log.info("Clicked (JavaScript) on element : " + by);
	        return;
	    } catch (Exception e) {
	        Log.warn("JS click failed on : " + by + " | Trying Actions click");
	    }

	    // 3️⃣ Actions click (last fallback)
	    try {
	        Actions actions = new Actions(driver);
	        actions.moveToElement(ele).click().perform();
	        Log.info("Clicked (Actions) on element : " + by);
	    } catch (Exception e) {
	        handleException(e, by, "CLICK");
	        Assert.fail("Unable to click element : " + by);
	    }
	}



	public void ClickByAction(By by) {

		Actions a = new Actions(driver);

		a.moveToElement(waitForExpectedElement(by)).click();

	}

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


	public void getTextNormal(By by, String value) {

		WebElement ele = waitForExpectedElement(by);

		value = ele.getText();

	}

	public String getTextnormalWithretun(By by) {

		WebElement ele = waitForExpectedElement(by);

		return ele.getText();

	}

	
	
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

	public void switchFrame(String id) {

		driver.switchTo().frame(id);

	}
	
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

	
	
	
	
	
	
	
	
	
}

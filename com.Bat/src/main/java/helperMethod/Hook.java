package helperMethod;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.aventstack.extentreports.MediaEntityBuilder;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.ExtentManager;
import utils.ExtentTestManager;

public class Hook extends GenricMethod{

	@Before
	public void beforeScenario(Scenario scenario) {
	    ExtentTestManager.startTest(scenario.getName());
	    launchApplication();
	}


	@After
	public void afterScenario(Scenario scenario) {

	    try {
	        String Screenshot =
	                ((TakesScreenshot) driver)
	                        .getScreenshotAs(OutputType.BASE64);

	        if (scenario.isFailed()) {
	            ExtentTestManager.getTest().fail(
	                    "Scenario Failed",
	                    MediaEntityBuilder
	                            .createScreenCaptureFromBase64String(Screenshot)
	                            .build()
	            );
	        } else {
	            ExtentTestManager.getTest().pass(
	                    "Scenario Passed",
	                    MediaEntityBuilder
	                            .createScreenCaptureFromBase64String(Screenshot)
	                            .build()
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    if (driver != null) {
	        driver.quit();
	    }
	}
	
	@AfterAll
	public static void afterall() {
	    ExtentManager.getReporter().flush();
	}

	

}

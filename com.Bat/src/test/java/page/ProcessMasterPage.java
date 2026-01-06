package page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import helperMethod.GenricMethod;
import utils.Log; // add Log utility import

/**
 * Page object for Process Master related actions.
 *
 * Each method logs its main steps using the project's `Log` helper so
 * test-run output and extent reports capture what the page object is doing.
 */
public class ProcessMasterPage extends GenricMethod {

	By MasterDataVerification = By.xpath("//ul[contains(@class,'main-menu')]//span[normalize-space()='Master Data']");

	/**
	 * Verify that the "Master Data" menu text is displayed and matches the expected value.
	 * Logs the verification steps before and after calling the underlying helper.
	 */
	public void clickingMasterData() {

		Log.info("[clickingMasterData] - Starting verification of Master Data text");

		String ExpectedDataMasterTect = "Master Data";

		try {
			// Call generic helper that will perform the text comparison and logging
			getDataWithTextComparison(MasterDataVerification, ExpectedDataMasterTect);

			// If we reach here, assume the helper logged success; mark explicit pass
			Log.pass("[clickingMasterData] - Verified Master Data text: " + ExpectedDataMasterTect);

		} catch (Exception e) {
			// On unexpected exceptions mark as fail and capture screenshot
			String ss = takeScreenShot("clickingMasterData_Error");
			Log.fail("[clickingMasterData] - Exception during verification: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e; // rethrow so tests can fail as expected
		}

		Log.info("[clickingMasterData] - Completed verification of Master Data text");

	}

	By clickingMaster = By.xpath("//ul[contains(@class,'main-menu')]//span[normalize-space()='Master Data']");

	/**
	 * Clicks the Master Data option in the main menu.
	 * Logs before attempting the click and after success.
	 */
	public void clickOnMasterOption() {

		Log.info("[clickOnMasterOption] - Attempting to click Master Data option: " + clickingMaster);

		try {
			// Use generic click helper which contains robust fallbacks and logging
			clickOnElement(clickingMaster);

			// If no exception, mark as passed
			Log.pass("[clickOnMasterOption] - Click action succeeded for: " + clickingMaster);

		} catch (AssertionError ae) {
			String ss = takeScreenShot("clickOnMasterOption_AssertionError");
			Log.fail("[clickOnMasterOption] - Assertion failed during click: " + ae.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw ae;
		} catch (Exception e) {
			String ss = takeScreenShot("clickOnMasterOption_Error");
			Log.fail("[clickOnMasterOption] - Exception during click: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}

		Log.info("[clickOnMasterOption] - Click action completed for Master Data option");

	}

	By MasterOptions = By.xpath("//li[@class='slide has-sub open']//ul//a");
	
	/**
	 * Verify that the 'Process' option exists inside the Master menu options.
	 * Logs the number of items found and the verification step.
	 */
	public void VerifyingProcessMasterISDisplayed() {
		
		Log.info("[VerifyingProcessMasterISDisplayed] - Locating master options using: " + MasterOptions);
		
		try {
			List<WebElement> Masters = driver.findElements(MasterOptions);
			
			// Log the count of options found to help debugging when tests fail
			Log.info("[VerifyingProcessMasterISDisplayed] - Number of master options found: " + Masters.size());
			
			String ExpectedMasterOptions = "Process";
			
			// Use generic method to search through the list and log result
			ReadDataFromList(Masters, MasterOptions, ExpectedMasterOptions);
			
			// Mark pass explicitly when the expected item was found —
			// ReadDataFromList logs on failure; to be robust we check presence here.
			boolean found = Masters.stream().anyMatch(e -> ExpectedMasterOptions.equals(e.getText().trim()));
			if (found) {
				Log.pass("[VerifyingProcessMasterISDisplayed] - Expected option found: " + ExpectedMasterOptions);
			} else {
				String ss = takeScreenShot("VerifyingProcessMasterISDisplayed_NotFound");
				Log.fail("[VerifyingProcessMasterISDisplayed] - Expected option NOT found: " + ExpectedMasterOptions);
				Log.info("Screenshot saved: " + ss);
				throw new AssertionError("Expected option not found: " + ExpectedMasterOptions);
			}
			
			Log.info("[VerifyingProcessMasterISDisplayed] - Completed verification for expected option: " + ExpectedMasterOptions);
		
		} catch (Exception e) {
			String ss = takeScreenShot("VerifyingProcessMasterISDisplayed_Error");
			Log.fail("[VerifyingProcessMasterISDisplayed] - Exception during verification: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}
		
	}
	
}
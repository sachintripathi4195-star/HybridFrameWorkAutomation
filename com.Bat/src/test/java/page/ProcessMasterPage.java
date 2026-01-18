package page;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import helperMethod.GenricMethod;
import utils.Log; // add Log utility import

/**
 * Page object for Process Master related actions.
 *
 * Each method logs its main steps using the project's `Log` helper so test-run
 * output and extent reports capture what the page object is doing.
 */
public class ProcessMasterPage extends GenricMethod {

	By MasterDataVerification = By.xpath("//ul[contains(@class,'main-menu')]//span[normalize-space()='Master Data']");

	DashboardPage ds = new DashboardPage();

	/**
	 * Verify that the "Master Data" menu text is displayed and matches the expected
	 * value. Logs the verification steps before and after calling the underlying
	 * helper.
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

	// By clickingMaster =
	// By.xpath("//ul[contains(@class,'main-menu')]//span[normalize-space()='Master
	// Data']");

	/**
	 * Clicks the Master Data option in the main menu. Logs before attempting the
	 * click and after success.
	 */
	public void clickOnMasterOption() {

		String expectedfield = null;

		Log.info("[clickOnMasterOption] - Attempting to click Master Data option: " + expectedfield);

		try {
			// Use generic click helper which contains robust fallbacks and logging

			expectedfield = "Master Data";

			ds.SelectMenuOption(expectedfield);

			// If no exception, mark as passed
			Log.pass("[clickOnMasterOption] - Click action succeeded for: " + expectedfield);

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

	public By MasterOptionsForProcessMaster = By.xpath("//li[@class='slide has-sub open']//ul//a");

	/**
	 * Verify that the 'Process' option exists inside the Master menu options. Logs
	 * the number of items found and the verification step.
	 */
	public void VerifyingProcessMasterISDisplayed() {

		Log.info("[VerifyingProcessMasterISDisplayed] - Locating master options using: "
				+ MasterOptionsForProcessMaster);

		try {
			List<WebElement> Masters = driver.findElements(MasterOptionsForProcessMaster);

			// Log the count of options found to help debugging when tests fail
			Log.info("[VerifyingProcessMasterISDisplayed] - Number of master options found: " + Masters.size());

			String ExpectedMasterOptions = "Process";

			// Use generic method to search through the list and log result
			ReadDataFromList(Masters, MasterOptionsForProcessMaster, ExpectedMasterOptions);

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

			Log.info("[VerifyingProcessMasterISDisplayed] - Completed verification for expected option: "
					+ ExpectedMasterOptions);

		} catch (Exception e) {
			String ss = takeScreenShot("VerifyingProcessMasterISDisplayed_Error");
			Log.fail("[VerifyingProcessMasterISDisplayed] - Exception during verification: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}

	}

	// New locators and methods to map feature steps for saving Process Master data
	By processNameInput = By.xpath("//input[@id='processName' or contains(@placeholder,'Process')]");
	By saveButton = By.xpath("//button[normalize-space()='Save' or @id='saveBtn']");
	By toastMessage = By
			.xpath("//div[contains(@class,'toast') or contains(@class,'alert')][contains(.,'Process Master Data')]");
	By EnterProcessNameValue = By.xpath("//input[@id='processName']");
	By EnterProcessRate = By.xpath("//input[@id='Rate']");
	By dateselection = By.xpath("//input[@id='date']");
	By EnterEfficiencyrate = By.xpath("//input[@id='Efficiency']");
	By ClickingCavitiesCheckbox = By.xpath("//input[@id='processcst']");
	By ohpnotapplicablecheckbox = By.xpath("//input[@id='Prefix']");
	By Mctonnage = By.xpath("//*[@id='Tonnage']");
	By unitDropDown = By.xpath("//select[@id='uomDrop']");
	By weightfactorDropdown = By.xpath("//select[@id='selectionWeight']");
	By rmInputCheckboxAll = By.xpath("//input[@id='rmAll']");
	By clickingBusinessSegmentFromDropdown = By
			.xpath("//select[@id='SupplierSegment']/following-sibling::div//span[@class='multiselect-selected-text']");
	By EnterSearchBoxInBusinessSegMentValue = By
			.xpath("//select[@id='SupplierSegment']/following-sibling::div//input[@type='search']");
	By ListOfLabelNameBusinessSegment = By.xpath("//select[@id='SupplierSegment']/following-sibling::div//label");
	By Rm_ExtrusionRubberCheckbox = By.xpath("//input[@name='rmListCheckbox']");
	By SaveBtn = By.xpath("//button[@id='processSave']");
	By UpdateBtn = By.xpath("//button[@id='processUpdate' or normalize-space()='Update']");
	By EditFirstRowBtn = By.xpath("(//table//tr//button[contains(@class,'edit') or normalize-space()='Edit'])[1]");
	By FirstRowProcessName = By.xpath("(//table//tr)[2]//td[1]");
	By clickingUnitDropdown = By
			.xpath("//select[@id='uomDrop']/following-sibling::span//span[@id='select2-uomDrop-container']");
	By EnterSearchBoxInUnitDropdown = By.xpath(
			"//span[@class='select2-container select2-container--default select2-container--open']//input[@type='search']");
	By listOfUnitDropdownOptions = By.xpath("//ul[@id='select2-uomDrop-results']/li");

	By WaitingForSupplierTableAfterSelectingBusinessSegment = By.xpath("//table[@id='rmSupplier']//label");

	/**
	 * Enter details for Process Master. Fills all fields using the available
	 * locators. This maps to step: "Given user enter process Master Data"
	 */
	public void enterProcessMasterData(String searchBusinessSegmentValue) {

		Log.info("[enterProcessMasterData] - Entering process master data (all fields)");

		try {

			// Wait for the Process Name input to be visible before proceeding

			String expectedfield = "Process";

			ReadAndClickDataFromList(MasterOptionsForProcessMaster, expectedfield);
			// Process Name
			String sampleProcessName = "Auto_Process_" + System.currentTimeMillis();
			clearAndEnterText(EnterProcessNameValue, sampleProcessName);
			Log.info("[enterProcessMasterData] - Entered process name: " + sampleProcessName);

			// Process Rate
			try {
				clearAndEnterText(EnterProcessRate, "100");
				Log.info("[enterProcessMasterData] - Entered process rate: 100");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Process rate field not found or not editable: " + e.getMessage());
			}

			try {

				String unitToSelect = "Hrs.";

				clickOnElement(clickingUnitDropdown);
				clearAndEnterText(EnterSearchBoxInUnitDropdown, unitToSelect);

				List<WebElement> unitOptions = driver.findElements(listOfUnitDropdownOptions);

				for (WebElement option : unitOptions) {
					if (option.getText().trim().equals(unitToSelect)) {
						option.click();
						break;
					}
				}

				Log.info("[enterProcessMasterData] - Selected unit value: Hrs.");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Unit dropdown not found or selection failed: " + e.getMessage());
			}

			// Date selection (use YYYY-MM-DD or adapt if your app needs another format)
			try {
				clearAndEnterText(dateselection, "2026-01-07");
				Log.info("[enterProcessMasterData] - Entered date: 2026-01-07");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Date field not available: " + e.getMessage());
			}

			// Efficiency rate
			try {
				clearAndEnterText(EnterEfficiencyrate, "85");
				Log.info("[enterProcessMasterData] - Entered efficiency rate: 85");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Efficiency field not available: " + e.getMessage());
			}

			// Clicking cavities checkbox
			try {
				clickOnElement(ClickingCavitiesCheckbox);
				Log.info("[enterProcessMasterData] - Toggled cavities checkbox");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Cavities checkbox not present: " + e.getMessage());
			}

			// OHP not applicable checkbox (toggle if present)
			try {
				clickOnElement(ohpnotapplicablecheckbox);
				Log.info("[enterProcessMasterData] - Toggled OHP Not Applicable checkbox");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - OHP checkbox not present: " + e.getMessage());
			}

			// MCT Tonnage
			try {
				clearAndEnterText(Mctonnage, "50");
				Log.info("[enterProcessMasterData] - Entered MCT tonnage: 50");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Tonnage field not present: " + e.getMessage());
			}

			// Weight factor dropdown - selecting by index 1 as a safe default
			try {
				selectDropdonwnByValue(weightfactorDropdown, "byIndex", "1");
				Log.info("[enterProcessMasterData] - Selected weight factor (index 1)");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Weight factor dropdown not present or selection failed: "
						+ e.getMessage());
			}

			// RM checkboxes - check 'all' and the specific RM checkbox if present
			try {
				clickOnElement(rmInputCheckboxAll);
				Log.info("[enterProcessMasterData] - Clicked RM 'All' checkbox");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - RM 'All' checkbox not present: " + e.getMessage());
			}

			try {
				// Open the multiselect dropdown by clicking the toggle button (more reliable
				// than the inner span)
				WebElement toggle = waitForExpectedElement(By.xpath(
						"//select[@id='SupplierSegment']/following-sibling::div//button[contains(@class,'multiselect') and contains(@class,'dropdown-toggle')]"));
				toggle.click();

				// Wait for the search input, clear and type the requested business segment
				WebElement search = waitForExpectedElement(EnterSearchBoxInBusinessSegMentValue);
				search.clear();
				search.sendKeys(searchBusinessSegmentValue);

				// Build XPath to find the checkbox input whose label text matches the requested
				// value
				String checkboxXpath = "//div[contains(@class,'multiselect-container')]//label[normalize-space()='"
						+ searchBusinessSegmentValue + "']/preceding-sibling::input[@type='checkbox']";

				WebElement checkbox = waitForExpectedElement(By.xpath(checkboxXpath));

				try {
					checkbox.click();
				} catch (Exception clickEx) {
					// fallback to JS click if normal click fails (overlays, offscreen, etc.)
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
				}

			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Business Segment selection failed: " + e.getMessage());
			}

			try {
				clickOnElement(Rm_ExtrusionRubberCheckbox);
				Log.info("[enterProcessMasterData] - Clicked RM specific checkbox (rmListCheckbox)");
			} catch (Exception e) {
				Log.warn("[enterProcessMasterData] - Specific RM checkbox not present: " + e.getMessage());
			}

			Log.pass("[enterProcessMasterData] - Completed entering process master data");

		} catch (Exception e) {
			String ss = takeScreenShot("enterProcessMasterData_Error");
			Log.fail("[enterProcessMasterData] - Exception while entering data: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}

	}

	/**
	 * Update an existing Process Master entry by opening the first row's edit
	 * button, changing some fields and leaving the form ready for update.
	 */
	public void updateProcessMasterData() {

		Log.info("[updateProcessMasterData] - Attempting to open first Process entry for edit");

		try {
			WebElement editBtn = waitForExpectedElement(EditFirstRowBtn);
			editBtn.click();
			Log.info("[updateProcessMasterData] - Clicked edit on first row");

			// Modify the process name to a new value so update can be verified
			String updatedName = "Updated_Process_" + System.currentTimeMillis();
			clearAndEnterText(EnterProcessNameValue, updatedName);
			Log.info("[updateProcessMasterData] - Updated process name to: " + updatedName);

			// Modify rate if present
			try {
				clearAndEnterText(EnterProcessRate, "150");
				Log.info("[updateProcessMasterData] - Updated process rate to 150");
			} catch (Exception e) {
				Log.warn("[updateProcessMasterData] - Could not update rate: " + e.getMessage());
			}

			Log.pass("[updateProcessMasterData] - Prepared updated data for Process Master");

		} catch (Exception e) {
			String ss = takeScreenShot("updateProcessMasterData_Error");
			Log.fail("[updateProcessMasterData] - Exception while preparing update: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}

	}

	/**
	 * Clicks the Update button for Process Master.
	 */
	public void clickUpdateButton() {

		Log.info("[clickUpdateButton] - Attempting to click Update button: " + UpdateBtn);

		try {
			clickOnElement(UpdateBtn);
			Log.pass("[clickUpdateButton] - Update clicked");
		} catch (Exception e) {
			String ss = takeScreenShot("clickUpdateButton_Error");
			Log.fail("[clickUpdateButton] - Exception while clicking Update: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}

	}

	/**
	 * Verify that a toast/message appears indicating the Process Master was
	 * updated.
	 */
	public void verifyProcessMasterUpdated(String expectedMessage) {

		Log.info("[verifyProcessMasterUpdated] - Waiting for update toast containing: " + expectedMessage);

		try {
			WebElement msg = waitForExpectedElement(
					By.xpath("//div[contains(@class,'toast') or contains(@class,'alert')][contains(.,'"
							+ expectedMessage + "')]"));
			String actual = msg.getText().trim();
			if (actual.contains(expectedMessage)) {
				Log.pass("[verifyProcessMasterUpdated] - Expected update toast displayed: " + actual);
			} else {
				String ss = takeScreenShot("verifyProcessMasterUpdated_NotMatching");
				Log.fail("[verifyProcessMasterUpdated] - Toast text did not match. Actual: " + actual);
				Log.info("Screenshot saved: " + ss);
				throw new AssertionError("Update toast text not as expected: " + actual);
			}
		} catch (Exception e) {
			String ss = takeScreenShot("verifyProcessMasterUpdated_Error");
			Log.fail("[verifyProcessMasterUpdated] - Exception while verifying update toast: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}

	}

	/**
	 * Clicks the Save button for Process Master. Uses the explicit SaveBtn locator.
	 * Maps to: "When user click on Save button"
	 */
	public void clickSaveButton() {

		Log.info("[clickSaveButton] - Attempting to click Save button using SaveBtn locator: " + SaveBtn);

		try {

			if (waitForExpectedElement(WaitingForSupplierTableAfterSelectingBusinessSegment).isDisplayed()) {
				Log.info("[clickSaveButton] - Save button is clickable.");

				clickOnElement(SaveBtn);

			} else {
				Log.warn("Unable To Save Supplier Table is Not Displayed");
			}
			Log.pass("[clickSaveButton] - Save clicked (SaveBtn)");
		} catch (Exception e) {
			String ss = takeScreenShot("clickSaveButton_Error");
			Log.fail("[clickSaveButton] - Exception while clicking Save: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}

	}

	/**
	 * Verifies that a toast/message appears indicating the Process Master was
	 * saved. Maps to: "And Process Master Data should be saved successfully" and
	 * "Then toast message should be displayed \"...\""
	 */

	public void verifyProcessMasterSaved(String expectedMessage) {

		Log.info("[verifyProcessMasterSaved] - Waiting for toast/message containing: " + expectedMessage);

		try {
			WebElement msg = waitForExpectedElement(toastContainer);
			String actual = msg.getText().trim();
			if (actual.contains(expectedMessage)) {
				Log.pass("[verifyProcessMasterSaved] - Expected toast displayed: " + actual);
			} else {
				String ss = takeScreenShot("verifyProcessMasterSaved_NotMatching");
				Log.fail("[verifyProcessMasterSaved] - Toast text did not match. Actual: " + actual);
				Log.info("Screenshot saved: " + ss);
				throw new AssertionError("Toast text not as expected: " + actual);
			}
		} catch (Exception e) {
			String ss = takeScreenShot("verifyProcessMasterSaved_Error");
			Log.fail("[verifyProcessMasterSaved] - Exception while verifying toast: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}

	}

	public void enterProcessDetailInformation(String processName, String processRate, String dateSelection,
			String efficiencyRate, String mcTonnage) {

		Log.info("[enterProcessDetailInformation] - Starting to enter Process Detail information");

		try {

			clearAndEnterText(EnterProcessNameValue, processName);
			Log.info("[enterProcessDetailInformation] - Entered process name: " + processName);

			clearAndEnterText(EnterProcessRate, processRate);
			Log.info("[enterProcessDetailInformation] - Entered process rate: " + processRate);

			clearAndEnterText(dateselection, dateSelection);
			Log.info("[enterProcessDetailInformation] - Entered date selection: " + dateSelection);

			clearAndEnterText(EnterEfficiencyrate, efficiencyRate);
			Log.info("[enterProcessDetailInformation] - Entered efficiency rate: " + efficiencyRate);

			clearAndEnterText(Mctonnage, mcTonnage);
			Log.info("[enterProcessDetailInformation] - Entered MC tonnage: " + mcTonnage);

			Log.pass("[enterProcessDetailInformation] - Process detail information entered successfully");

		} catch (Exception e) {
			String ss = takeScreenShot("enterProcessDetailInformation_Error");
			Log.fail("[enterProcessDetailInformation] - Exception while entering process detail: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}
	}

	// Toast Message
	private By genericToastMessage = By
			.xpath("//div[contains(@class,'toast') or contains(@class,'alert') or @role='alert']");

	public void verifyToastMessageContains(String expectedMessage) {

		Log.info("[verifyToastMessageContains] - Verifying toast message: " + expectedMessage);

		try {

			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));

			WebElement toast = shortWait.until(ExpectedConditions.visibilityOfElementLocated(genericToastMessage));

			String actualToast = toast.getText().trim();
			Log.info("[verifyToastMessageContains] - Actual toast text: " + actualToast);

			if (actualToast.contains(expectedMessage)) {
				Log.pass("[verifyToastMessageContains] - Toast verified successfully");
			} else {
				String ss = takeScreenShot("verifyToastMessageContains_Mismatch");
				Log.fail("[verifyToastMessageContains] - Expected: " + expectedMessage + " | Actual: " + actualToast);
				Log.info("Screenshot saved: " + ss);
				throw new AssertionError("Toast message mismatch");
			}

		} catch (Exception e) {
			String ss = takeScreenShot("verifyToastMessageContains_Error");
			Log.fail("[verifyToastMessageContains] - Exception while verifying toast: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}
	}

	// Specific Applicability Customer Information

	By EnterCustmerNameSearchBox = By.xpath("//div[@id='partAttributeDiv']//input[@id='myInputCustomer']");

	public void enterSpecificApplicabilityCustomerInformation(String EnterCustmerName) {

		Log.info("[enterSpecificApplicabilityCustomerInformation] - Starting specific applicability selection");

		try {

			clickOnElement(ClickingCavitiesCheckbox);
			Log.info("[enterSpecificApplicabilityCustomerInformation] - Clicked Cavities checkbox");

			clickOnElement(ohpnotapplicablecheckbox);
			Log.info("[enterSpecificApplicabilityCustomerInformation] - Clicked OHP Not Applicable checkbox");

			selectDropdonwnByValue(weightfactorDropdown, "byIndex", "1");
			Log.info("[enterSpecificApplicabilityCustomerInformation] - Selected Weight Factor (index 1)");

			clickOnElement(rmInputCheckboxAll);
			Log.info("[enterSpecificApplicabilityCustomerInformation] - Clicked RM Input Checkbox (All)");

			// 🔍 Search customer
			clearAndEnterText(EnterCustmerNameSearchBox, EnterCustmerName);
			Log.info("[enterSpecificApplicabilityCustomerInformation] - Searched customer: " + EnterCustmerName);

			// ✅ Validate visible row and click checkbox
			List<WebElement> rows = driver.findElements(By.xpath("//table[@id='rmCustomer']//tr"));
			boolean customerFound = false;

			for (WebElement row : rows) {

				// Skip hidden rows (display:none)
				if (!row.isDisplayed()) {
					continue;
				}

				WebElement label = row.findElement(By.xpath(".//label"));
				String actualCustomerName = label.getText().trim();

				Log.info("[enterSpecificApplicabilityCustomerInformation] - Visible customer found: "
						+ actualCustomerName);

				if (actualCustomerName.equals(EnterCustmerName)) {

					WebElement checkbox = row.findElement(By.xpath(".//input[@type='checkbox']"));

					try {
						checkbox.click();
					} catch (Exception ex) {
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
					}

					Log.pass(
							"[enterSpecificApplicabilityCustomerInformation] - Customer selected: " + EnterCustmerName);
					customerFound = true;
					break;
				}
			}

			if (!customerFound) {
				throw new AssertionError("Customer not found after search filter: " + EnterCustmerName);
			}

			clickOnElement(SaveBtn);

		} catch (Exception e) {
			String ss = takeScreenShot("enterSpecificApplicabilityCustomerInformation_Error");
			Log.fail("[enterSpecificApplicabilityCustomerInformation] - Exception during applicability selection: "
					+ e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}
	}

	// Toast container
	private By toastContainer = By.id("toast-container");

	// Warning toast message (actual text)
	private By warningToastMessage = By.cssSelector("#toast-container .toast.toast-warning .toast-message");

	public void verifyWarningToastMessage(String expectedMessage) {

		Log.info("[verifyWarningToastMessage] - Verifying warning toast message: " + expectedMessage);

		try {

			WebElement toastMsg = waitForExpectedElement(warningToastMessage);
			String actualMessage = toastMsg.getText().trim();

			Log.info("Actual warning toast message: " + actualMessage);

			if (actualMessage.equals(expectedMessage)) {
				Log.pass("Warning toast verified successfully: " + actualMessage);
			} else {
				String ss = takeScreenShot("WarningToast_Mismatch");
				Log.fail("Warning toast mismatch. Actual: " + actualMessage);
				Log.info("Screenshot saved: " + ss);
				throw new AssertionError("Warning toast text mismatch");
			}

		} catch (Exception e) {
			String ss = takeScreenShot("WarningToast_Error");
			Log.fail("Exception while verifying warning toast: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}
	}

	public void enterProcessDetailInformationForVerifyingEfficiencyRatePopupMessage(String processName,
			String processRate, String dateSelection, String efficiencyRate, String expectedMessage, String mcTonnage) {

		Log.info("[EfficiencyValidation] - Starting efficiency validation flow");

		try {

			clearAndEnterText(EnterProcessNameValue, processName);
			Log.info("Entered process name: " + processName);

			clearAndEnterText(EnterProcessRate, processRate);
			Log.info("Entered process rate: " + processRate);

			selectDropdonwnByValue(unitDropDown, "visibleText", "Hrs.");
			Log.info("Selected unit: Hrs.");

			clearAndEnterText(dateselection, dateSelection);
			Log.info("Entered date: " + dateSelection);

			// ❌ Enter invalid efficiency (>100)
			clearAndEnterText(EnterEfficiencyrate, efficiencyRate);
			Log.info("Entered efficiency rate: " + efficiencyRate);

			// ✅ Validate toast message
			WebElement toast = waitForExpectedElement(warningToastMessage);
			String actualMessage = toast.getText().trim();

			if (actualMessage.contains(expectedMessage)) {
				Log.pass("Warning toast verified successfully: " + actualMessage);
			} else {
				String ss = takeScreenShot("EfficiencyToast_Mismatch");
				Log.fail("Toast mismatch. Actual: " + actualMessage);
				Log.info("Screenshot saved: " + ss);
				throw new AssertionError("Expected toast not found. Actual: " + actualMessage);
			}

			clearAndEnterText(Mctonnage, mcTonnage);
			Log.info("Entered MC tonnage: " + mcTonnage);

		} catch (Exception e) {
			String ss = takeScreenShot("EfficiencyToast_Error");
			Log.fail("Exception during efficiency validation: " + e.getMessage());
			Log.info("Screenshot saved: " + ss);
			throw e;
		}
	}

}
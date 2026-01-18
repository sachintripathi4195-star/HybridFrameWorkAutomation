package stepdefination;

import helperMethod.GenricMethod;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import page.ProcessMasterPage;
import utils.RandomDataExtractor;

public class ProcessMasterStep {

    GenricMethod gen = new GenricMethod();
    ProcessMasterPage ppage = new ProcessMasterPage();

    // ===========================
    // Navigation & Menu Steps
    // ===========================

    @When("Master Data Should be Displayed")
    public void master_data_should_be_displayed() {
        ppage.clickingMasterData();
    }

    @When("user click MasterData")
    public void user_click_master_data() {
        ppage.clickOnMasterOption();
    }

    // ✅ REQUIRED for first two scenarios
    @Then("Process Master Should be displayed")
    public void process_master_should_be_displayed() {
        ppage.VerifyingProcessMasterISDisplayed();
    }

    // Used in validation scenarios
    @Then("Process Master option should be displayed")
    public void process_master_option_should_be_displayed() {
        ppage.VerifyingProcessMasterISDisplayed();
    }

    @When("user click on Process Master")
    public void user_click_on_process_master() {
        ProcessMasterPage.ReadAndClickDataFromList(
                ppage.MasterOptionsForProcessMaster,
                "Process"
        );
    }

    // ===========================
    // Save Process Master
    // ===========================

    @Given("user enter process Master Data")
    public void user_enter_process_master_data() {

        String processName = RandomDataExtractor.genrateNameWithnumber("????");

        ppage.enterProcessMasterData(
                "rishi"
        );
    }

    @When("user click on Save button")
    public void user_click_on_save_button() {
        ppage.clickSaveButton();
    }

    // ===========================
    // Process Rate Validations
    // ===========================

    @Given("user enter process detail information without process rate")
    public void user_enter_process_detail_information_without_process_rate() {

        String processName = "Auto_Process_" + System.currentTimeMillis();

        ppage.enterProcessDetailInformation(
                processName,
                "", // ❌ missing rate
                "15-03-2025",
                "80",
                "20"
        );
    }

    @Given("user enter process detail information with invalid process rate")
    public void user_enter_process_detail_information_with_invalid_process_rate() {

        String processName = "Auto_Process_" + System.currentTimeMillis();

        ppage.enterProcessDetailInformation(
                processName,
                "test", // ❌ invalid rate
                "15-03-2025",
                "80",
                "20"
        );
    }

    // ===========================
    // Applicability
    // ===========================

    @Given("user enter specific applicability customer information")
    public void user_enter_specific_applicability_customer_information() {

        String customerName = gen.process.getProperty("CustomerName");
        ppage.enterSpecificApplicabilityCustomerInformation(customerName);
    }

    // ===========================
    // Efficiency Validation
    // ===========================

    @Given("user enter process detail information with efficiency more than 100")
    public void user_enter_process_detail_information_with_efficiency_more_than_100() {

        String processName = RandomDataExtractor.genrateNameWithnumber("????");
        String processRate = RandomDataExtractor.randomIntegerNumber();

        ppage.enterProcessDetailInformationForVerifyingEfficiencyRatePopupMessage(
                processName,
                processRate,
                "15-03-2025",
                "5000", // ❌ efficiency > 100
                "Efficiency cannot exceed 100.",
                "20"
        );
    }

    // ===========================
    // Toast Validation (Single, Clean)
    // ===========================

    @Then("warning toast message {string} should be displayed")
    public void warning_toast_message_should_be_displayed(String expectedMessage) {
        ppage.verifyWarningToastMessage(expectedMessage);
    }
}

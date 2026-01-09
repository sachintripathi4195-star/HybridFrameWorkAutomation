package stepdefination;

import helperMethod.GenricMethod;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import page.ProcessMasterPage;

public class ProcessMasterStep {

    GenricMethod gen = new GenricMethod();
    ProcessMasterPage ppage = new ProcessMasterPage();

    @When("Master Data Should be Displayed")
    public void master_data_should_be_displayed() {
        ppage.clickingMasterData();  
    }

    @When("user click MasterData")
    public void user_click_master_data() {
        ppage.clickOnMasterOption();
    }

    @Then("Process Master Should be displayed")
    public void process_master_should_be_displayed() {
        ppage.VerifyingProcessMasterISDisplayed();
    }

    @Given("user enter process Master Data")
    public void user_enter_process_master_data() {
        ppage.enterProcessMasterData("rishi");
    }

    @When("user click on Save button")
    public void user_click_on_save_button() {
        ppage.clickSaveButton();
    }

  
    

   

}

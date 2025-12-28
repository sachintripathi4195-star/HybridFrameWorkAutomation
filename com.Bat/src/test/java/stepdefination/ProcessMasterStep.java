package stepdefination;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import page.ProcessMasterPage;

public class ProcessMasterStep {
	
	ProcessMasterPage pp = new ProcessMasterPage();
	
	@When("Master Data Should be Displayed")
	public void master_data_should_be_open() {
	  
		pp.clickingMasterData();
		
	}

	@When("user click MasterData")
	public void user_click_master_data() {
	  
		pp.clickOnMasterOption();
	}

	
	@Then("Process Master Should be displayed")
	public void process_master_should_be_displayed() {
	   
		pp.VerifyingProcessMasterISDisplayed();
		
	}

}

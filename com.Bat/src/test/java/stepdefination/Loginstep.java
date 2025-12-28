package stepdefination;

import helperMethod.GenricMethod;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import page.LoginPage;

public class Loginstep {

	GenricMethod gen = new GenricMethod();
	LoginPage lpage =new LoginPage();
	
	
	
	@When("navigate to application")
	public void navigatetoapplication() {

		lpage.verifyNavigateToApplication();
	}

	@When("user enters valid credentials")
	public void userEntersValidCredentials() throws InterruptedException {

		lpage.clickLogingButton();

			}

	@Then("user should be logged in successfully")
	public void userShouldBeLoggedInSuccessfully() {

	lpage.ValidateuserLoggedinOrnot();
	
	
	}
}

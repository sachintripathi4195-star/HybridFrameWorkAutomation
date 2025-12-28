package page;

import org.openqa.selenium.By;
import helperMethod.GenricMethod;
import utils.Log;

public class LoginPage extends GenricMethod {

	By clickloginButton = By.xpath("//button[@type='submit']");
	By EnterUserId = By.id("Email");
	By EnterUserPassword = By.id("Password");

	public void clickLogingButton() {

		clearAndEnterText(EnterUserId, prop.getProperty("userName"));

		Log.info("Write user name");

		clearAndEnterText(EnterUserPassword, prop.getProperty("password"));

		Log.info("Write password");

		clickOnElement(clickloginButton);

		Log.info("clicked login button sucessFully....");

	}

	public void verifyNavigateToApplication() {
		
		
		String ActualTille = GenricMethod.driver.getTitle();
		String Expectedtittle = "Costmasters-Sign In";

		if (ActualTille.equals(Expectedtittle)) {

			System.out.println("Navigated SucessFully");
		} else {

			System.out.println("url Not SucessFully Launch or NetWork Issue");
		}
		
		
	}
	
	By dashboardele = By.xpath("//ul[@class='main-menu active']/descendant::span");
	public void ValidateuserLoggedinOrnot() {
	

	String dashboardText = waitForExpectedElement(dashboardele).getText(); 
	System.out.println(dashboardText);
	GenricMethod.takeScreenShot("dashboard ScreenShot");
	if (dashboardText.equalsIgnoreCase("dashboardText")) {

		System.out.println("Confirmed Logged In SucessFully....");

	}

	
}
	
	
}

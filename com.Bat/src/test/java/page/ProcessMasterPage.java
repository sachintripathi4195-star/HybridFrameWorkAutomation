package page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import helperMethod.GenricMethod;

public class ProcessMasterPage extends GenricMethod {

	By MasterDataVerification = By.xpath("//ul[contains(@class,'main-menu')]//span[normalize-space()='Master Data']");

	public void clickingMasterData() {

		String ExpectedDataMasterTect = "Master Data";
		getDataWithTextComparison(MasterDataVerification, ExpectedDataMasterTect);

	}

	By clickingMaster = By.xpath("//ul[contains(@class,'main-menu')]//span[normalize-space()='Master Data']");

	public void clickOnMasterOption() {

		clickOnElement(clickingMaster);

	}

	By MasterOptions = By.xpath("//li[@class='slide has-sub open']//ul//a");
	
	public void VerifyingProcessMasterISDisplayed() {
		
		List<WebElement> Masters = driver.findElements(MasterOptions);
		
		String ExpectedMasterOptions = "Process";
		
		ReadDataFromList(Masters,MasterOptions,ExpectedMasterOptions);
		
		
	}
	
}

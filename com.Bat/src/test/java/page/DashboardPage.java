package page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import helperMethod.GenricMethod;

public class DashboardPage extends GenricMethod{

	// locator updated to select all span elements within the main menu
	By MasterDataVerification = By.xpath("//ul[contains(@class,'main-menu')]//span");
	
	
	
	public void SelectMenuOption(String optionName) {
		List<WebElement> menuOptions = driver.findElements(MasterDataVerification);
		WebElement target = null;
		// try to find and click the element normally
		for (WebElement option : menuOptions) {
			try {
				String optionText = option.getText();
				if (optionText != null && optionText.trim().equalsIgnoreCase(optionName)) {
					target = option;
					option.click();
					return; // clicked successfully
				}
			} catch (Exception e) {
				// if a transient error occurs while reading/clicking this option, continue to next
			}
		}

		// if normal click didn't work but we found the element, try JS click as a fallback
		if (target != null) {
			try {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("arguments[0].click();", target);
				return;
			} catch (Exception e) {
				// will try a final attempt below
			}
		}

		// final attempt: locate by exact text and click via JS (covers cases where list changed)
		try {
			WebElement el = driver.findElement(By.xpath("//ul[contains(@class,'main-menu')]//span[normalize-space()=\""+optionName+"\"]"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
			return;
		} catch (Exception e) {
			throw new RuntimeException("Unable to locate or click the '" + optionName + "' menu option", e);
		}
		  
	}
	
	
	
}
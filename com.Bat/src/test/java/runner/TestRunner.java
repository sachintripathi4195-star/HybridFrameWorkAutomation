package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features",
        glue = {"stepdefination", "helperMethod"},
        plugin = {
                "pretty",
                "json:target/cucumber-json/cucumber.json" 
        },
        tags = "@Reg",  
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
	
	
	
}



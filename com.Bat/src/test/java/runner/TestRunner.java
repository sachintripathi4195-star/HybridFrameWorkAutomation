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
        tags = "@smoke or @Reg",  
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
	
	//tree /F /A > project-structure.txt

	
}

//Give me command to run this using maven in cmd.
// To run the TestRunner using Maven in the command line, use the following command:
// mvn clean test -Dtest=runner.TestRunner
//GIVE ME COMMAND WHERE WE CAN MOVE OUR TERMINAL TO PROJECT DIRECTORY AND RUN THE ABOVE COMMAND IN A SINGLE LINE
// To navigate to the project directory and run the TestRunner in a single line, use the following command:

//give complete directory path of project in below command
//cd C:\Automation\HybridFrameWorkAutomation\com.Bat
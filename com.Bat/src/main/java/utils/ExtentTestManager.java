package utils;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void startTest(String testName) {
        ExtentTest extentTest =
                ExtentManager.getReporter().createTest(testName);
        test.set(extentTest);
    }

    public static void endTest() {
        // Nothing here
    }
    
    
    
    
}

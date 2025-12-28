package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getReporter() {

        if (extent == null) {     

            String reportPath =
                    System.getProperty("user.dir") + "/reports/AutomationReport.html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("QA_Tester", "Sachindra Mani Tripathi");
            extent.setSystemInfo("Project_Name", "Practice");
        }
        return extent;
    }
}
    
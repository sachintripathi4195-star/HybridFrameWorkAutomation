package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.Status;

public class Log {

    public static Logger log = LogManager.getLogger(Log.class);

    public static void info(String message) {
        log.info(message);

        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.INFO, message);
        }
    }

    public static void warn(String message) {
        log.warn(message);

        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.WARNING, message);
        }
    }

    public static void error(String message) {
        log.error(message);

        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.FAIL, message);
        }
    } 

    public static void debug(String message) {
        log.debug(message);

        
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.INFO, "[DEBUG] " + message);
        }
    }

    /**
     * Mark a step as passed both in the logger and the Extent report.
     * Use this to make a successful assertion/step explicit in reports.
     *
     * @param message descriptive pass message
     */
    public static void pass(String message) {
        log.info(message);

        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.PASS, message);
        }
    }

    /**
     * Mark a step as failed both in the logger and the Extent report.
     * Prefer calling this when a validation fails and you want a clear
     * failure message in the report. This is similar to error(...) but
     * provided as an explicit semantic helper.
     *
     * @param message descriptive failure message
     */
    public static void fail(String message) {
        log.error(message);

        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.FAIL, message);
        }
    }
}
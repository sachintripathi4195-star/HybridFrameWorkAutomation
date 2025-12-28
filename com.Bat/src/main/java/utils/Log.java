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
}

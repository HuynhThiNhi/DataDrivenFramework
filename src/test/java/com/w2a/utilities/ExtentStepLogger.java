package com.w2a.utilities;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.w2a.base.TestBase;
import org.openqa.selenium.WebDriver;

/**
 * Utility class for managing Extent test steps and logging
 * Provides methods to log detailed test steps with screenshots
 */
public class ExtentStepLogger {
    
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static int stepCounter = 0;
    
    /**
     * Sets the current ExtentTest instance
     * @param extentTest The ExtentTest instance
     */
    public static void setTest(ExtentTest extentTest) {
        test.set(extentTest);
        stepCounter = 0; // Reset step counter for new test
    }
    
    /**
     * Gets the current ExtentTest instance
     * @return The current ExtentTest instance
     */
    public static ExtentTest getTest() {
        return test.get();
    }
    
    /**
     * Logs a test step with INFO status
     * @param stepDescription Description of the step
     */
    public static void logStep(String stepDescription) {
        stepCounter++;
        if (getTest() != null) {
            getTest().log(Status.INFO, "Step " + stepCounter + ": " + stepDescription);
        }
        TestBase.logInfo("Step " + stepCounter + ": " + stepDescription);
    }
    
    /**
     * Logs a test step with PASS status
     * @param stepDescription Description of the step
     */
    public static void logPass(String stepDescription) {
        stepCounter++;
        if (getTest() != null) {
            getTest().log(Status.PASS, "Step " + stepCounter + ": " + stepDescription);
        }
        TestBase.logInfo("✓ Step " + stepCounter + ": " + stepDescription);
    }
    
    /**
     * Logs a test step with FAIL status
     * @param stepDescription Description of the step
     */
    public static void logFail(String stepDescription) {
        stepCounter++;
        if (getTest() != null) {
            getTest().log(Status.FAIL, "Step " + stepCounter + ": " + stepDescription);
        }
        TestBase.logError("✗ Step " + stepCounter + ": " + stepDescription);
    }
    
    /**
     * Logs a test step with WARNING status
     * @param stepDescription Description of the step
     */
    public static void logWarning(String stepDescription) {
        stepCounter++;
        if (getTest() != null) {
            getTest().log(Status.WARNING, "Step " + stepCounter + ": " + stepDescription);
        }
        TestBase.logWarning("⚠ Step " + stepCounter + ": " + stepDescription);
    }
    
    /**
     * Logs a test step with screenshot
     * @param stepDescription Description of the step
     * @param driver WebDriver instance for screenshot
     * @param screenshotName Name for the screenshot
     */
    public static void logStepWithScreenshot(String stepDescription, WebDriver driver, String screenshotName) {
        stepCounter++;
        if (getTest() != null) {
            try {
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, screenshotName);
                if (screenshotPath != null) {
                    getTest().log(Status.INFO, "Step " + stepCounter + ": " + stepDescription,
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } else {
                    getTest().log(Status.INFO, "Step " + stepCounter + ": " + stepDescription);
                }
            } catch (Exception e) {
                getTest().log(Status.INFO, "Step " + stepCounter + ": " + stepDescription);
                TestBase.logger.warn("Failed to capture screenshot for step: " + e.getMessage());
            }
        }
        TestBase.logInfo("Step " + stepCounter + ": " + stepDescription);
    }
    
    /**
     * Logs a test step with PASS status and screenshot
     * @param stepDescription Description of the step
     * @param driver WebDriver instance for screenshot
     * @param screenshotName Name for the screenshot
     */
    public static void logPassWithScreenshot(String stepDescription, WebDriver driver, String screenshotName) {
        stepCounter++;
        if (getTest() != null) {
            try {
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, screenshotName);
                if (screenshotPath != null) {
                    getTest().log(Status.PASS, "Step " + stepCounter + ": " + stepDescription,
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } else {
                    getTest().log(Status.PASS, "Step " + stepCounter + ": " + stepDescription);
                }
            } catch (Exception e) {
                getTest().log(Status.PASS, "Step " + stepCounter + ": " + stepDescription);
                TestBase.logger.warn("Failed to capture screenshot for step: " + e.getMessage());
            }
        }
        TestBase.logInfo("✓ Step " + stepCounter + ": " + stepDescription);
    }
    
    /**
     * Logs a test step with FAIL status and screenshot
     * @param stepDescription Description of the step
     * @param driver WebDriver instance for screenshot
     * @param screenshotName Name for the screenshot
     */
    public static void logFailWithScreenshot(String stepDescription, WebDriver driver, String screenshotName) {
        stepCounter++;
        if (getTest() != null) {
            try {
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, screenshotName);
                if (screenshotPath != null) {
                    getTest().log(Status.FAIL, "Step " + stepCounter + ": " + stepDescription,
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } else {
                    getTest().log(Status.FAIL, "Step " + stepCounter + ": " + stepDescription);
                }
            } catch (Exception e) {
                getTest().log(Status.FAIL, "Step " + stepCounter + ": " + stepDescription);
                TestBase.logger.warn("Failed to capture screenshot for step: " + e.getMessage());
            }
        }
        TestBase.logError("✗ Step " + stepCounter + ": " + stepDescription);
    }
    
    /**
     * Logs test data information
     * @param dataType Type of data (e.g., "Test Data", "Expected Result")
     * @param dataValue The actual data value
     */
    public static void  logTestData(String dataType, String dataValue) {
        if (getTest() != null) {
            getTest().log(Status.INFO, "<b>" + dataType + ":</b> " + dataValue);
        }
        TestBase.logInfo(dataType + ": " + dataValue);
    }
    
    /**
     * Logs test verification/assertion
     * @param verificationDescription Description of what is being verified
     * @param expectedValue Expected value
     * @param actualValue Actual value
     * @param isPassed Whether the verification passed
     */
    public static void logVerification(String verificationDescription, String expectedValue, String actualValue, boolean isPassed) {
        stepCounter++;
        String status = isPassed ? "PASSED" : "FAILED";
        String message = "Step " + stepCounter + ": Verification - " + verificationDescription + 
                        " | Expected: " + expectedValue + " | Actual: " + actualValue + " | Status: " + status;
        
        if (getTest() != null) {
            if (isPassed) {
                getTest().log(Status.PASS, message);
            } else {
                getTest().log(Status.FAIL, message);
            }
        }
        
        if (isPassed) {
            TestBase.logInfo("✓ " + message);
        } else {
            TestBase.logError("✗ " + message);
        }
    }
    
    /**
     * Logs test section header
     * @param sectionName Name of the test section
     */
    public static void logSection(String sectionName) {
        if (getTest() != null) {
            getTest().log(Status.INFO, "<b><font color='blue'>=== " + sectionName + " ===</font></b>");
        }
        TestBase.logInfo("=== " + sectionName + " ===");
    }
    
    /**
     * Logs test start information
     * @param testName Name of the test
     * @param testDescription Description of the test
     */
    public static void logTestStart(String testName, String testDescription) {
        if (getTest() != null) {
            getTest().log(Status.INFO, "<b>Test:</b> " + testName);
            getTest().log(Status.INFO, "<b>Description:</b> " + testDescription);
            getTest().log(Status.INFO, "<b>Start Time:</b> " + new java.util.Date());
        }
        TestBase.logInfo("Starting Test: " + testName + " - " + testDescription);
    }
    
    /**
     * Logs test end information
     * @param testName Name of the test
     * @param testStatus Final status of the test
     */
    public static void logTestEnd(String testName, String testStatus) {
        if (getTest() != null) {
            getTest().log(Status.INFO, "<b>End Time:</b> " + new java.util.Date());
            getTest().log(Status.INFO, "<b>Final Status:</b> " + testStatus);
            getTest().log(Status.INFO, "<b>Total Steps:</b> " + stepCounter);
        }
        TestBase.logInfo("Test Completed: " + testName + " - Status: " + testStatus + " - Total Steps: " + stepCounter);
    }
    
    /**
     * Resets the step counter
     */
    public static void resetStepCounter() {
        stepCounter = 0;
    }
    
    /**
     * Gets the current step counter
     * @return Current step number
     */
    public static int getCurrentStepNumber() {
        return stepCounter;
    }
}

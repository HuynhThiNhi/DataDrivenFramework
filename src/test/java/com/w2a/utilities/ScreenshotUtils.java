package com.w2a.utilities;

import com.w2a.base.TestBase;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing screenshots for ReportNG
 * Provides methods to take screenshots and integrate with ReportNG
 */
public class ScreenshotUtils {
    
    private static final String SCREENSHOT_DIR = "target/surefire-reports/screenshots/";
    private static final String RELATIVE_SCREENSHOT_DIR = "screenshots/";
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    
    /**
     * Captures a screenshot and returns the file path
     * @param driver WebDriver instance
     * @param testName Name of the test for file naming
     * @return Path to the screenshot file
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            
            // Generate timestamp for unique file names
            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + fileName;
            String relativePath = RELATIVE_SCREENSHOT_DIR + fileName;
            
            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(filePath);
            
            // Copy file to destination
            FileUtils.copyFile(sourceFile, destinationFile);
            
            TestBase.logger.info("Screenshot captured: " + filePath);
            return relativePath; // Return relative path for ReportNG
            
        } catch (IOException e) {
            TestBase.logger.error("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Captures screenshot and adds it to ReportNG report
     * @param driver WebDriver instance
     * @param testName Name of the test
     * @param description Description for the screenshot
     */
    public static void captureScreenshotForReport(WebDriver driver, String testName, String description) {
        String screenshotPath = captureScreenshot(driver, testName);
        if (screenshotPath != null) {
            // Add screenshot to ReportNG
            Reporter.log("<br><a href='" + screenshotPath + "' target='_blank'>" + description + "</a><br>");
            Reporter.log("<br><img src='" + screenshotPath + "' height='200' width='300'/><br>");
        }
    }
    
    /**
     * Captures screenshot on test failure
     * @param driver WebDriver instance
     * @param testName Name of the test
     */
    public static void captureFailureScreenshot(WebDriver driver, String testName) {
        String screenshotPath = captureScreenshot(driver, testName + "_FAILED");
        if (screenshotPath != null) {
            Reporter.log("<br><b>Test Failed - Screenshot:</b><br>");
            Reporter.log("<br><a href='" + screenshotPath + "' target='_blank'>View Screenshot</a><br>");
            Reporter.log("<br><img src='"  + screenshotPath + "' height='200' width='300'/><br>");
        }
    }
    
    /**
     * Captures screenshot on test success
     * @param driver WebDriver instance
     * @param testName Name of the test
     */
    public static void captureSuccessScreenshot(WebDriver driver, String testName) {
        String screenshotPath = captureScreenshot(driver, testName + "_PASSED");

        if (screenshotPath != null) {
            Reporter.log("<br><b>Test Passed - Screenshot:</b><br>");
            Reporter.log("<br><a href='" + screenshotPath + "' target='_blank'>View Screenshot</a><br>");
            Reporter.log("<br><img src='" + screenshotPath + "' height='200' width='300'/><br>");
        }
    }
}

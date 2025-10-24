package com.w2a.listeners;

import com.w2a.base.TestBase;
import com.w2a.utilities.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Custom ReportNG Listener for enhanced reporting
 * Captures screenshots and adds detailed information to reports
 */
public class ReportNGListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        TestBase.logger.info("Starting test: " + result.getMethod().getMethodName());
        Reporter.log("<br><b>Test Started:</b> " + result.getMethod().getMethodName() + "<br>");
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        TestBase.logger.info("Test passed: " + result.getMethod().getMethodName());
        Reporter.log("<br><b>Test Status:</b> <span style='color: green;'>PASSED</span><br>");
        
        // Capture success screenshot
        if (TestBase.getDriver() != null) {
            ScreenshotUtils.captureSuccessScreenshot(TestBase.getDriver(), result.getMethod().getMethodName());
        }
        
        // Add test details
        addTestDetails(result);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        TestBase.logger.error("Test failed: {}", result.getMethod().getMethodName());
        Reporter.log("<br><b>Test Status:</b> <span style='color: red;'>FAILED</span><br>");
        
        // Capture failure screenshot
        if (TestBase.getDriver() != null) {
            ScreenshotUtils.captureFailureScreenshot(TestBase.getDriver(), result.getMethod().getMethodName());
        }
        
        // Add failure details
        addFailureDetails(result);
        addTestDetails(result);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        TestBase.logger.warn("Test skipped: {}", result.getMethod().getMethodName());
        Reporter.log("<br><b>Test Status:</b> <span style='color: orange;'>SKIPPED</span><br>");
        addTestDetails(result);
    }
    
    @Override
    public void onStart(ITestContext context) {
        TestBase.logger.info("Test suite started: {}", context.getName());
        Reporter.log("<br><h2>Test Suite: " + context.getName() + "</h2><br>");
        Reporter.log("<br><b>Start Time:</b> " + new java.util.Date() + "<br>");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        TestBase.logger.info("Test suite finished: {}", context.getName());
        Reporter.log("<br><b>End Time:</b> " + new java.util.Date() + "<br>");
        Reporter.log("<br><b>Total Tests:</b> " + context.getAllTestMethods().length + "<br>");
        Reporter.log("<br><b>Passed:</b> " + context.getPassedTests().size() + "<br>");
        Reporter.log("<br><b>Failed:</b> " + context.getFailedTests().size() + "<br>");
        Reporter.log("<br><b>Skipped:</b> " + context.getSkippedTests().size() + "<br>");
    }
    
    /**
     * Adds test details to the report
     * @param result Test result
     */
    private void addTestDetails(ITestResult result) {
        Reporter.log("<br><b>Test Method:</b> " + result.getMethod().getMethodName() + "<br>");
        Reporter.log("<br><b>Test Class:</b> " + result.getTestClass().getName() + "<br>");
        Reporter.log("<br><b>Duration:</b> " + (result.getEndMillis() - result.getStartMillis()) + " ms<br>");
        
        // Add parameters if any
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            Reporter.log("<br><b>Test Parameters:</b><br>");
            for (int i = 0; i < parameters.length; i++) {
                Reporter.log("&nbsp;&nbsp;Parameter " + (i + 1) + ": " + parameters[i] + "<br>");
            }
        }
    }
    
    /**
     * Adds failure details to the report
     * @param result Test result
     */
    private void addFailureDetails(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Reporter.log("<br><b>Failure Reason:</b> " + throwable.getMessage() + "<br>");
            Reporter.log("<br><b>Stack Trace:</b><br>");
            Reporter.log("<pre>" + getStackTrace(throwable) + "</pre>");
        }
    }
    
    /**
     * Gets stack trace as string
     * @param throwable Exception
     * @return Stack trace string
     */
    private String getStackTrace(Throwable throwable) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}

package com.w2a.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.w2a.base.TestBase;
import com.w2a.utilities.ExtentReportManager;
import com.w2a.utilities.ExtentStepLogger;
import com.w2a.utilities.ScreenshotUtils;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomListeners extends TestBase implements ITestListener, ISuiteListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onStart(ISuite suite) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportPath = System.getProperty("user.dir") + "/target/extent-reports/ExtentReport.html";
        extent = ExtentReportManager.createInstance(reportPath);
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName())
                .assignCategory(result.getTestClass().getRealClass().getSimpleName());
        test.set(extentTest);
        
        // Set the test in ExtentTestManager for step logging
        ExtentStepLogger.setTest(extentTest);
        
        test.get().log(Status.INFO, "Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentStepLogger.logTestEnd(result.getMethod().getMethodName(), "PASSED");
        test.get().log(Status.PASS, "Test Passed");
        attachScreenshot(result, "_PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentStepLogger.logTestEnd(result.getMethod().getMethodName(), "FAILED");
        test.get().log(Status.FAIL, result.getThrowable());
        attachScreenshot(result, "_FAILED");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentStepLogger.logTestEnd(result.getMethod().getMethodName(), "SKIPPED");
        test.get().log(Status.SKIP, "Test Skipped");
    }

    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }
    @Override public void onStart(ITestContext context) { }
    @Override public void onFinish(ITestContext context) { }

    private void attachScreenshot(ITestResult result, String suffix) {
        try {
            if (driver != null) {
                String name = result.getMethod().getMethodName() + suffix;
                String path = ScreenshotUtils.captureScreenshot(driver, name);
                if (path != null) {
                    test.get().info("Screenshot:", MediaEntityBuilder.createScreenCaptureFromPath(path).build());
                }
            }
        } catch (Exception e) {
            TestBase.logger.warn("Unable to attach screenshot: " + e.getMessage());
        }
    }
}

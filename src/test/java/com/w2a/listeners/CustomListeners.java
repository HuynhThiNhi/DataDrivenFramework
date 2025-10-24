package com.w2a.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.w2a.base.TestBase;
import com.w2a.utilities.ExtentReportManager;
import com.w2a.utilities.ExtentStepLogger;
import com.w2a.utilities.ScreenshotUtils;
import com.w2a.utilities.MonitoringMail;
import com.w2a.utilities.MailRequest;
import com.w2a.utilities.MailConfig;
import com.w2a.utilities.TestConfig;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import javax.mail.MessagingException;
import java.io.File;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CustomListeners extends TestBase implements ITestListener, ISuiteListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    
    // Thread-safe test execution tracking
    private static final AtomicInteger totalTests = new AtomicInteger(0);
    private static final AtomicInteger passedTests = new AtomicInteger(0);
    private static final AtomicInteger failedTests = new AtomicInteger(0);
    private static final AtomicInteger skippedTests = new AtomicInteger(0);
    private static final AtomicLong suiteStartTime = new AtomicLong(0);
    private static volatile String suiteName;
    
    // Thread-safe driver access is handled by TestBase

    @Override
    public void onStart(ISuite suite) {
        suiteName = suite.getName();
        suiteStartTime.set(System.currentTimeMillis());
        
        // Reset counters atomically
        totalTests.set(0);
        passedTests.set(0);
        failedTests.set(0);
        skippedTests.set(0);
        
        String reportPath = System.getProperty("user.dir") + "/target/extent-reports/ExtentReport.html";
        extent = ExtentReportManager.createInstance(reportPath);
        
        TestBase.logInfo("Test Suite Started: " + suiteName);
        ExtentStepLogger.logStep("Test Suite Started: " + suiteName);
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extent != null) {
            extent.flush();
        }
        
        // Calculate execution time
        long executionTime = System.currentTimeMillis() - suiteStartTime.get();
        long executionTimeSeconds = executionTime / 1000;
        long executionTimeMinutes = executionTimeSeconds / 60;
        
        TestBase.logInfo("Test Suite Completed: " + suiteName);
        TestBase.logInfo("Total Tests: " + totalTests.get() + ", Passed: " + passedTests.get() + 
                        ", Failed: " + failedTests.get() + ", Skipped: " + skippedTests.get());
        TestBase.logInfo("Execution Time: " + executionTimeMinutes + " minutes " + 
                        (executionTimeSeconds % 60) + " seconds");
        
        // Send email notification
        sendTestExecutionReport(executionTime);
    }

//    triggerd before each test method begins execution. It happens after @BeforeClass and @BeforeMethod
    @Override
    public void onTestStart(ITestResult result) {
        totalTests.incrementAndGet();
        // Get the test from ThreadLocal and log success
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName())
                .assignCategory(result.getTestClass().getRealClass().getSimpleName());
        test.set(extentTest);
        
        // Set the test in ExtentTestManager for step logging
        ExtentStepLogger.setTest(extentTest);

        test.get().log(Status.INFO, "Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests.incrementAndGet();
        ExtentStepLogger.logTestEnd(result.getMethod().getMethodName(), "PASSED");
        test.get().log(Status.PASS, "Test Passed");
        attachScreenshot(result, "_PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedTests.incrementAndGet();
        ExtentStepLogger.logTestEnd(result.getMethod().getMethodName(), "FAILED");
        test.get().log(Status.FAIL, result.getThrowable());
        attachScreenshot(result, "_FAILED");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests.incrementAndGet();
        ExtentStepLogger.logTestEnd(result.getMethod().getMethodName(), "SKIPPED");
        test.get().log(Status.SKIP, "Test Skipped");
    }

    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }
    @Override public void onStart(ITestContext context) { }
    @Override public void onFinish(ITestContext context) { }

    private void attachScreenshot(ITestResult result, String suffix) {
        try {
            // Get driver from TestBase or ThreadLocal
            WebDriver driver = getDriverForTest(result);
            if (driver != null) {
                String name = result.getMethod().getMethodName() + "_" + Thread.currentThread().getId() + suffix;
                String path = ScreenshotUtils.captureScreenshot(driver, name);
                if (path != null) {
                    test.get().info("Screenshot:", MediaEntityBuilder.createScreenCaptureFromPath(path).build());
                }
            }
        } catch (Exception e) {
            TestBase.logger.warn("Unable to attach screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Get WebDriver instance for the current test
     * Uses TestBase ThreadLocal methods for consistency
     */
    private WebDriver getDriverForTest(ITestResult result) {
        try {
            // Use TestBase ThreadLocal methods
            return TestBase.getDriver();
        } catch (Exception e) {
            TestBase.logger.warn("Could not get driver for test: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Send test execution report via email
     * @param executionTime Total execution time in milliseconds
     */
    private void sendTestExecutionReport(long executionTime) {
        try {
            ExtentStepLogger.logStep("Preparing test execution report email");
            
            // Create mail configuration
            MailConfig config = new MailConfig();
            config.setSmtpHost(TestConfig.server);
            config.setUsername(TestConfig.from);
            config.setPassword(TestConfig.password);
            config.setSmtpPort(587);
            config.setUseStartTLS(true);
            config.setUseSSL(false);
            config.setDebug(false);
            
            // Create email request
            MailRequest request = new MailRequest();
            request.setFrom(TestConfig.from);
            request.setTo(Arrays.asList(TestConfig.to));
            request.setSubject("Test Execution Report - " + suiteName + " - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            request.setHtmlContent(generateEmailContent(executionTime));
            request.setPriority(failedTests.get() > 0 ? 1 : 3); // High priority if tests failed
            
            // Add Extent report as attachment
            String reportPath = System.getProperty("user.dir") + "/target/extent-reports/";
            File reportDir = new File(reportPath);
            if (reportDir.exists()) {
                File[] reportFiles = reportDir.listFiles((dir, name) -> name.endsWith(".html"));
                if (reportFiles != null && reportFiles.length > 0) {
                    // Get the latest report file
                    File latestReport = reportFiles[reportFiles.length - 1];
                    request.addAttachment(new MailRequest.Attachment(latestReport.getAbsolutePath(), "TestReport.html"));
                }
            }
            
            // Send email
            MonitoringMail mailService = new MonitoringMail(config);
            mailService.sendMail(request);
            
            ExtentStepLogger.logPass("Test execution report email sent successfully");
            TestBase.logInfo("Test execution report email sent to: " + Arrays.toString(TestConfig.to));
            
        } catch (Exception e) {
            ExtentStepLogger.logFail("Failed to send test execution report email: " + e.getMessage());
            TestBase.logError("Failed to send test execution report email: " + e.getMessage());
        }
    }
    
    /**
     * Generate HTML content for email
     * @param executionTime Total execution time in milliseconds
     * @return HTML content string
     */
    private String generateEmailContent(long executionTime) {
        long executionTimeSeconds = executionTime / 1000;
        long executionTimeMinutes = executionTimeSeconds / 60;
        long remainingSeconds = executionTimeSeconds % 60;
        
        int failedCount = failedTests.get();
        int passedCount = passedTests.get();
        int skippedCount = skippedTests.get();
        int totalCount = totalTests.get();
        
        String statusColor = failedCount > 0 ? "#ff4444" : "#44aa44"; // Red if failed, green if passed
        String statusText = failedCount > 0 ? "FAILED" : "PASSED";
        String statusIcon = failedCount > 0 ? "❌" : "✅";
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append(".header { background-color: #f0f0f0; padding: 15px; border-radius: 5px; }");
        html.append(".summary { background-color: #f9f9f9; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        html.append(".status { color: ").append(statusColor).append("; font-weight: bold; font-size: 18px; }");
        html.append(".stats { display: flex; justify-content: space-around; margin: 20px 0; }");
        html.append(".stat-box { text-align: center; padding: 15px; border: 1px solid #ddd; border-radius: 5px; margin: 5px; }");
        html.append(".passed { background-color: #d4edda; border-color: #c3e6cb; }");
        html.append(".failed { background-color: #f8d7da; border-color: #f5c6cb; }");
        html.append(".skipped { background-color: #fff3cd; border-color: #ffeaa7; }");
        html.append(".total { background-color: #e2e3e5; border-color: #d6d8db; }");
        html.append("</style></head><body>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>").append(statusIcon).append(" Test Execution Report</h1>");
        html.append("<p><strong>Suite:</strong> ").append(suiteName).append("</p>");
        html.append("<p><strong>Execution Time:</strong> ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("</p>");
        html.append("</div>");
        
        // Summary
        html.append("<div class='summary'>");
        html.append("<h2 class='status'>Overall Status: ").append(statusText).append("</h2>");
        html.append("<p><strong>Execution Duration:</strong> ").append(executionTimeMinutes).append(" minutes ").append(remainingSeconds).append(" seconds</p>");
        html.append("</div>");
        
        // Statistics
        html.append("<div class='stats'>");
        html.append("<div class='stat-box total'><h3>").append(totalCount).append("</h3><p>Total Tests</p></div>");
        html.append("<div class='stat-box passed'><h3>").append(passedCount).append("</h3><p>Passed</p></div>");
        html.append("<div class='stat-box failed'><h3>").append(failedCount).append("</h3><p>Failed</p></div>");
        html.append("<div class='stat-box skipped'><h3>").append(skippedCount).append("</h3><p>Skipped</p></div>");
        html.append("</div>");
        
        // Additional information
        html.append("<div class='summary'>");
        html.append("<h3>Test Summary</h3>");
        html.append("<ul>");
        if (totalCount > 0) {
            html.append("<li><strong>Pass Rate:</strong> ").append(String.format("%.1f", (double)passedCount/totalCount*100)).append("%</li>");
            html.append("<li><strong>Failure Rate:</strong> ").append(String.format("%.1f", (double)failedCount/totalCount*100)).append("%</li>");
            html.append("<li><strong>Skip Rate:</strong> ").append(String.format("%.1f", (double)skippedCount/totalCount*100)).append("%</li>");
        } else {
            html.append("<li><strong>No tests executed</strong></li>");
        }
        html.append("</ul>");
        html.append("<p><em>Detailed test report is attached to this email.</em></p>");
        
        // Add Jenkins report link
        try {
            String jenkinsReportUrl = generateJenkinsReportUrl();
            html.append("<div style='margin-top: 20px; padding: 15px; background-color: #e3f2fd; border-left: 4px solid #2196f3; border-radius: 4px;'>");
            html.append("<h4 style='margin: 0 0 10px 0; color: #1976d2;'>📊 View Detailed Report Online</h4>");
            html.append("<p style='margin: 0 0 10px 0;'>Access the interactive HTML report in Jenkins:</p>");
            html.append("<a href='").append(jenkinsReportUrl).append("' style='display: inline-block; padding: 10px 20px; background-color: #2196f3; color: white; text-decoration: none; border-radius: 4px; font-weight: bold;'>");
            html.append("🔗 Open Jenkins Report");
            html.append("</a>");
            html.append("<p style='margin: 10px 0 0 0; font-size: 12px; color: #666;'>");
            html.append("URL: ").append(jenkinsReportUrl);
            html.append("</p>");
            html.append("</div>");
        } catch (Exception e) {
            TestBase.logWarning("Failed to generate Jenkins URL: " + e.getMessage());
            html.append("<div style='margin-top: 20px; padding: 15px; background-color: #fff3cd; border-left: 4px solid #ffc107; border-radius: 4px;'>");
            html.append("<p style='margin: 0; color: #856404;'>⚠️ Jenkins report URL could not be generated. Please check the Jenkins server configuration.</p>");
            html.append("</div>");
        }
        
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }
    
    /**
     * Generate Jenkins report URL
     * @return Jenkins report URL string
     */
    private String generateJenkinsReportUrl() {
        try {
            // Use configured Jenkins host or fallback to local IP
            String jenkinsHost = TestConfig.jenkinsHost;
            
            // If localhost is configured, try to get actual IP
            if ("localhost".equalsIgnoreCase(jenkinsHost) || "127.0.0.1".equals(jenkinsHost)) {
                jenkinsHost = InetAddress.getLocalHost().getHostAddress();
            }
            
            return "http://" + jenkinsHost + ":" + TestConfig.jenkinsPort + TestConfig.jenkinsJobPath;
            
        } catch (Exception e) {
            // Fallback to configured host if IP resolution fails
            return "http://" + TestConfig.jenkinsHost + ":" + TestConfig.jenkinsPort + TestConfig.jenkinsJobPath;
        }
    }
}

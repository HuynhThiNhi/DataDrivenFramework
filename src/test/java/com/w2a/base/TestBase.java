package com.w2a.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import com.w2a.utilities.ExcelReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;



public class TestBase {
	//again test2
	/*
	 * WebDriver - done Properties - done Logs - log4j jar, .log,
	 * log4j2.properties, Logger ExtentReports DB Excel Mail ReportNG,
	 * ExtentReports Jenkins
	 *
	 */

	// ThreadLocal WebDriver for parallel execution
	private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
	private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();
	
	// Static properties (shared across threads)
    public static Properties config = new Properties();
    public static Properties OR = new Properties();
    public static FileInputStream fis;
	public static Logger logger = LogManager.getLogger(TestBase.class);
	public static ExcelReader excel = new ExcelReader(System.getProperty("user.dir") + "/src/test/resources/excel/testdata.xlsx");
	public static String browser;
	
	// Thread-safe getters
	public static WebDriver getDriver() {
		return driverThreadLocal.get();
	}
	
	public static WebDriverWait getWait() {
		return waitThreadLocal.get();
	}
	
//	// Backward compatibility
//	public static WebDriver driver = getDriver();
//	public static WebDriverWait wait = getWait();

	@BeforeSuite
	public void setUp() {
		logger.info("Start setup test module");
        try {
            fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/Config.properties"));
            config.load(fis);

            fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/OR.properties"));
            OR.load(fis);

            if (System.getenv("browser") != null && !System.getenv("browser").isEmpty()) {
                browser = System.getenv("browser");
                System.setProperty("browser", browser);
            } else {
                browser = config.getProperty("browser");
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
	
	@BeforeMethod
	public void setUpDriver() {
		// Initialize WebDriver for current thread
		WebDriver currentDriver = driverThreadLocal.get();
		if (currentDriver == null) {
			currentDriver = createDriver();
			driverThreadLocal.set(currentDriver);
			waitThreadLocal.set(new WebDriverWait(currentDriver, Duration.ofSeconds(Integer.parseInt(config.getProperty("explicit.wait")))));
			
//			// Update static references for backward compatibility
//			driver = currentDriver;
//			wait = waitThreadLocal.get();
			
			logger.info("WebDriver initialized for thread: " + Thread.currentThread().getId());
		}
	}
	
	@AfterMethod
	public void tearDownDriver() {
		// Clean up WebDriver for current thread
		WebDriver currentDriver = driverThreadLocal.get();
		if (currentDriver != null) {
			try {
				currentDriver.quit();
				logger.info("WebDriver closed for thread: " + Thread.currentThread().getId());
			} catch (Exception e) {
				logger.warn("Error closing WebDriver: " + e.getMessage());
			} finally {
				driverThreadLocal.remove();
				waitThreadLocal.remove();
				
				// Clear static references
//				driver = null;
//				wait = null;
			}
		}
	}
	
	private WebDriver createDriver() {
		WebDriver newDriver = null;
		if (browser.equalsIgnoreCase("chrome")) {
			WebDriverManager.chromedriver().setup();
			newDriver = new ChromeDriver();
		} else if (browser.equalsIgnoreCase("safari")) {
			WebDriverManager.safaridriver().setup();
			newDriver = new SafariDriver();
		}
		
		if (newDriver != null) {
			newDriver.manage().window().fullscreen();
			newDriver.get(config.getProperty("testsiteurl"));
			newDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(config.getProperty("implicit.wait"))));
		}
		
		return newDriver;
	}

	/**
	 * Utility method to click an element using locator from OR.properties
	 * @param locatorKey Key from OR.properties file
	 */
	public void click(String locatorKey) {
		// Implementation can be added here if needed
		// Currently using Page Object Model approach
	}

	/**
	 * Utility method to type text into an element using locator from OR.properties
	 * @param locatorKey Key from OR.properties file
	 * @param value Text to type
	 */
	public void type(String locatorKey, String value) {
		// Implementation can be added here if needed
		// Currently using Page Object Model approach
	}

	public boolean isElementPresent(By by) {
		try {
			WebDriver currentDriver = getDriver();
			if (currentDriver != null) {
				currentDriver.findElement(by);
				return true;
			}
			return false;
		} catch (NoSuchElementException e) {
			logger.error("No such element: ", e);
			return false;
		}
	}

	public static void verifyEquals(String expected, String actual) throws IOException {
		try {
			org.testng.Assert.assertEquals(actual, expected);
			Reporter.log("<br><b>Verification Passed:</b> Expected: " + expected + ", Actual: " + actual + "<br>");
		} catch (Throwable t) {
			Reporter.log("<br><b>Verification Failed:</b> " + t.getMessage() + "<br>");
			// Capture screenshot for ReportNG
			WebDriver currentDriver = getDriver();
			if (currentDriver != null) {
				com.w2a.utilities.ScreenshotUtils.captureFailureScreenshot(currentDriver, "Verification_Failed");
			}
			throw t;
		}
	}

	/**
	 * Logs information to ReportNG
	 * @param message Message to log
	 */
	public static void logInfo(String message) {
		Reporter.log("<br><b>INFO:</b> " + message + "<br>");
		logger.info(message);
	}

	/**
	 * Logs error to ReportNG
	 * @param message Error message to log
	 */
	public static void logError(String message) {
		Reporter.log("<br><b style='color: red;'>ERROR:</b> " + message + "<br>");
		logger.error(message);
	}

	/**
	 * Logs warning to ReportNG
	 * @param message Warning message to log
	 */
	public static void logWarning(String message) {
		Reporter.log("<br><b style='color: orange;'>WARNING:</b> " + message + "<br>");
		logger.warn(message);
	}
	
	/**
	 * Set driver for current thread (for parallel execution)
	 * @param driver WebDriver instance
	 */
	public static void setDriver(WebDriver driver) {
		driverThreadLocal.set(driver);
//		TestBase.driver = driver; // Update static reference
	}
	
	/**
	 * Clear driver for current thread
	 */
	public static void clearDriver() {
		driverThreadLocal.remove();
//		TestBase.driver = null; // Clear static reference
	}
	
	/**
	 * Check if driver is available for current thread
	 * @return true if driver is available, false otherwise
	 */
	public static boolean isDriverAvailable() {
		return driverThreadLocal.get() != null;
	}
	
	/**
	 * Get current thread ID for debugging
	 * @return current thread ID
	 */
	public static long getCurrentThreadId() {
		return Thread.currentThread().getId();
	}

	@AfterSuite(alwaysRun = true)
	public void tearDown() {
		// Clean up any remaining ThreadLocal drivers
		WebDriver currentDriver = driverThreadLocal.get();
		if (currentDriver != null) {
			try {
				currentDriver.quit();
                logger.info("Final cleanup: WebDriver closed for thread: {}", Thread.currentThread().getId());
			} catch (Exception e) {
                logger.warn("Error in final cleanup: {}", e.getMessage());
			} finally {
				driverThreadLocal.remove();
				waitThreadLocal.remove();
			}
		}
		
		// Close file streams
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				logger.warn("Error closing file stream: " + e.getMessage());
			}
		}
		
		logger.info("Test suite execution completed!");
	}
}

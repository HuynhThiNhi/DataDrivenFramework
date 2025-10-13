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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;



public class TestBase {
	//again test2
	/*
	 * WebDriver - done Properties - done Logs - log4j jar, .log,
	 * log4j2.properties, Logger ExtentReports DB Excel Mail ReportNG,
	 * ExtentReports Jenkins
	 *
	 */

	public static WebDriver driver;
    public static Properties config = new Properties();
    public static Properties OR = new Properties();
    public static FileInputStream fis;
	public static Logger logger = LogManager.getLogger(TestBase.class);
	public static ExcelReader excel = new ExcelReader(System.getProperty("user.dir") + "/src/test/resources/excel/testdata.xlsx");
	public static WebDriverWait wait;

	@BeforeSuite
	public void setUp() {
		logger.info("Start setup test module");
        if (driver == null) {
            try {
                fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/Config.properties"));
                config.load(fis);

				fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/OR.properties"));
				OR.load(fis);

				if (config.getProperty("browser").equalsIgnoreCase("chrome")) {
					WebDriverManager.chromedriver().setup();
					driver = new ChromeDriver();
				}

				driver.manage().window().fullscreen();
				driver.get(config.getProperty("testsiteurl"));
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(config.getProperty("implicit.wait"))));
				wait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(config.getProperty("explicit.wait"))));

            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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

			driver.findElement(by);
			return true;

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
			if (driver != null) {
				com.w2a.utilities.ScreenshotUtils.captureFailureScreenshot(driver, "Verification_Failed");
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

	@AfterSuite(alwaysRun = true)
	public void tearDown() {
//
		if (driver != null) {
			driver.quit();
		}

//		log.debug("test execution completed !!!");
	}
}

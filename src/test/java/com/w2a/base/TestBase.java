package com.w2a.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;



public class TestBase {
	//again test2
	/*
	 * WebDriver - done Properties - done Logs - log4j jar, .log,
	 * log4j.properties, Logger ExtentReports DB Excel Mail ReportNG,
	 * ExtentReports Jenkins
	 *
	 */

	public static WebDriver driver;
    public static Properties config = new Properties();
    public static Properties OR = new Properties();
    public static FileInputStream fis;

	@BeforeSuite
	public void setUp() {
        if (driver == null) {
            try {
                Properties config = new Properties();
                Properties OR = new Properties();
                FileInputStream fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/Config.properties"));
                config.load(fis);

                fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/OR.properties"));
                OR.load(fis);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

	public void click(String locator) {


	}

	public void type(String locator, String value) {



	}

	static WebElement dropdown;

	public void select(String locator, String value) {

//		if (locator.endsWith("_CSS")) {
//			dropdown = driver.findElement(By.cssSelector(OR.getProperty(locator)));
//		} else if (locator.endsWith("_XPATH")) {
//			dropdown = driver.findElement(By.xpath(OR.getProperty(locator)));
//		} else if (locator.endsWith("_ID")) {
//			dropdown = driver.findElement(By.id(OR.getProperty(locator)));
//		}
//
//		Select select = new Select(dropdown);
//		select.selectByVisibleText(value);
//
//		CustomListeners.testReport.get().log(Status.INFO, "Selecting from dropdown : " + locator + " value as " + value);

	}

	public boolean isElementPresent(By by) {

//		try {
//
//			driver.findElement(by);
//			return true;
//
//		} catch (NoSuchElementException e) {
//
//			return false;
//
//		}
        return true;

	}

	public static void verifyEquals(String expected, String actual) throws IOException {

//		try {
//
//			Assert.assertEquals(actual, expected);
//
//		} catch (Throwable t) {
//
//			TestUtil.captureScreenshot();
//			// ReportNG
//			Reporter.log("<br>" + "Verification failure : " + t.getMessage() + "<br>");
//			Reporter.log("<a target=\"_blank\" href=" + TestUtil.screenshotName + "><img src=" + TestUtil.screenshotName
//					+ " height=200 width=200></img></a>");
//			Reporter.log("<br>");
//			Reporter.log("<br>");
//			// Extent Reports
//			CustomListeners.testReport.get().log(Status.FAIL, " Verification failed with exception : " + t.getMessage());
//			//CustomListeners.testReport.get().log(Status.FAIL, CustomListeners.testReport.get().addScreenCaptureFromPath(TestUtil.screenshotName));
//
//		}

	}

	@AfterSuite
	public void tearDown() {
//
//		if (driver != null) {
//			driver.quit();
//		}
//
//		log.debug("test execution completed !!!");
	}
}

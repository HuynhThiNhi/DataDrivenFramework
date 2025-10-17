package com.w2a.testcases;

import com.w2a.base.TestBase;
import com.w2a.utilities.ExtentStepLogger;

import com.w2a.utilities.TestUtil;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Hashtable;

public class AddCustomerTest extends TestBase {

    @BeforeClass
    public void loginAsManager() {
        driver.get(config.getProperty("testsiteurl"));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(OR.getProperty("bmlBtn_CSS")))).click();
    }

    @Test(dataProviderClass = TestUtil.class, dataProvider = "dp")
    public void addCustomerTest(Hashtable<String, String> data) {

        if (!data.get("runmode").equals("Y")) {
            throw new SkipException("Skipping as run mode is 'N'");
        }
        
        // Log test start information
        ExtentStepLogger.logTestStart("Add Customer Test", "Test to add a new customer with provided details");
        
        // Log test data
        ExtentStepLogger.logTestData("First Name", data.get("firstname"));
        ExtentStepLogger.logTestData("Last Name", data.get("lastname"));
        ExtentStepLogger.logTestData("Post Code", data.get("postcode"));
        ExtentStepLogger.logTestData("Expected Alert Text", data.get("allerttext"));
        
        try {
            ExtentStepLogger.logSection("Customer Addition Process");
            
            // Navigate to Add Customer page
            ExtentStepLogger.logStep("Click on Add Customer button");
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(OR.getProperty("addCustBtn_CSS")))).click();
            ExtentStepLogger.logPassWithScreenshot("Successfully clicked Add Customer button", driver, "AddCustomer_Clicked");

            // Fill customer form
            ExtentStepLogger.logSection("Customer Form Filling");
            
            ExtentStepLogger.logStep("Enter first name: " + data.get("firstname"));
            driver.findElement(By.cssSelector(OR.getProperty("firstname_CSS"))).sendKeys(data.get("firstname"));
            ExtentStepLogger.logPass("Successfully entered first name: " + data.get("firstname"));

            ExtentStepLogger.logStep("Enter last name: " + data.get("lastname"));
            driver.findElement(By.cssSelector(OR.getProperty("lastname_CSS"))).sendKeys(data.get("lastname"));
            ExtentStepLogger.logPass("Successfully entered last name: " + data.get("lastname"));

            ExtentStepLogger.logStep("Enter post code: " + data.get("postcode"));
            driver.findElement(By.cssSelector(OR.getProperty("postcode_CSS"))).sendKeys(data.get("postcode"));
            ExtentStepLogger.logPassWithScreenshot("Successfully entered post code: " + data.get("postcode"), driver, "Form_Filled");

            // Submit form
            ExtentStepLogger.logSection("Form Submission");
            ExtentStepLogger.logStep("Click on Add Customer submit button");
            driver.findElement(By.cssSelector(OR.getProperty("addbtn_CSS"))).click();
            ExtentStepLogger.logPass("Successfully submitted customer form");

            // Handle alert
            ExtentStepLogger.logSection("Alert Handling and Verification");
            ExtentStepLogger.logStep("Wait for alert to appear");
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            ExtentStepLogger.logStep("Alert appeared with text: " + alertText);
            
            // Verification
            ExtentStepLogger.logStep("Verify alert text contains expected text");
            boolean alertVerification = alertText.contains(data.get("alerttext"));
            ExtentStepLogger.logVerification("Alert Text Verification", data.get("alerttext"), alertText, alertVerification);
            
            Assert.assertTrue(alertVerification, 
                "Alert text '" + alertText + "' does not contain expected text '" + data.get("alerttext") + "'");
            
            ExtentStepLogger.logStep("Accept the alert");
            alert.accept();
            ExtentStepLogger.logPassWithScreenshot("Successfully accepted alert and completed customer addition", driver, "Customer_Added_Success");
            

        } catch (Exception e) {
            ExtentStepLogger.logFailWithScreenshot("Test failed for customer: " + data.get("firstname") + " " + data.get("lastname") + " - " + e.getMessage(), driver, "Customer_Add_Failed");
            TestBase.logError("Test failed for customer: " + data.get("firstname") + " " + data.get("lastname") + " - " + e.getMessage());
            throw e;
        }
    }

//    @DataProvider
//    Object[][] getData(){
//        String sheetName = "AddCustomerTest";
//        int rows = excel.getRowCount(sheetName);
//        int cols = 4; // Only need 3 columns: firstName, lastName, postCode
//        Object[][] data = new Object[rows-1][cols];
//
//        for (int r = 2; r <= rows; r++) {
//            for (int c = 0; c < cols; c++) {
//                data[r-2][c] = excel.getCellData(sheetName, r, c);
//            }
//        }
//        return data;
//    }

    public static void main(String[] args) {
        String sheetName = "AddCustomerTest";
        int rows = excel.getRowCount(sheetName);
        int cols = 4; // Only need 3 columns: firstName, lastName, postCode
        Object[][] data = new Object[rows-1][cols];

        for (int r = 2; r <= rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r-2][c] = excel.getCellData(sheetName, c, r);
            }
        }
    }
}

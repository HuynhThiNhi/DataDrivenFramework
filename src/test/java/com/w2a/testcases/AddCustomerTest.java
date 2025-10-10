package com.w2a.testcases;

import com.w2a.base.TestBase;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddCustomerTest extends TestBase {

    @BeforeClass
    public void loginAsManager() {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(OR.getProperty("bmlBtn_CSS")))).click();
    }

    @Test(dataProvider = "getData") 
    public void addCustomer(String firstName, String lastName, String postCode, String allertText) {
        
        // Log test start to ReportNG
        TestBase.logInfo("Starting Add Customer test for: " + firstName + " " + lastName);
        
        try {
            // Navigate to Add Customer page
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(OR.getProperty("addCustBtn_CSS")))).click();
            TestBase.logInfo("Clicked Add Customer button");
            
            // Fill customer form
            driver.findElement(By.cssSelector(OR.getProperty("firstname_CSS"))).sendKeys(firstName);
            TestBase.logInfo("Entered first name: " + firstName);
            
            driver.findElement(By.cssSelector(OR.getProperty("lastname_CSS"))).sendKeys(lastName);
            TestBase.logInfo("Entered last name: " + lastName);
            
            driver.findElement(By.cssSelector(OR.getProperty("postcode_CSS"))).sendKeys(postCode);
            TestBase.logInfo("Entered postcode: " + postCode);
            
            // Submit form
            driver.findElement(By.cssSelector(OR.getProperty("addbtn_CSS"))).click();
            TestBase.logInfo("Submitted customer form");

            // Handle alert
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            TestBase.logInfo("Alert text: " + alertText);
            
            Assert.assertTrue(alertText.contains(allertText), 
                "Alert text '" + alertText + "' does not contain expected text '" + allertText + "'");
            
            alert.accept();
            TestBase.logInfo("Customer added successfully: " + firstName + " " + lastName);
            
        } catch (Exception e) {
            TestBase.logError("Test failed for customer: " + firstName + " " + lastName + " - " + e.getMessage());
            throw e;
        }
    }

    @DataProvider
    Object[][] getData(){
        String sheetName = "AddCustomerTest";
        int rows = excel.getRowCount(sheetName);
        int cols = 4; // Only need 3 columns: firstName, lastName, postCode
        Object[][] data = new Object[rows-1][cols];

        for (int r = 2; r <= rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r-2][c] = excel.getCellData(sheetName, r, c);
            }
        }
        return data;
    }

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

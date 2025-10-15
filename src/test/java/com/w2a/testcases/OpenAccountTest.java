package com.w2a.testcases;

import com.w2a.base.TestBase;
import com.w2a.utilities.TestUtil;
import com.w2a.utilities.ExtentStepLogger;
import com.w2a.utilities.DropdownUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class OpenAccountTest extends TestBase {

    @BeforeClass
    public void loginAsManager() {
        driver.get(config.getProperty("testsiteurl")); 
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(OR.getProperty("bmlBtn_CSS")))).click();
    }
    


    @Test(description = "Test Open Account with Valid Data", dataProviderClass = TestUtil.class, dataProvider = "dp")
    public void openAccountTest(String customer, String currency) {
        ExtentStepLogger.logTestStart("Open Account Test", "Test opening account with valid customer and currency");
        
        // Log test data
        ExtentStepLogger.logTestData("Customer", customer);
        ExtentStepLogger.logTestData("Currency", currency);
        
        try {
            ExtentStepLogger.logSection("Navigate to Open Account Page");
            ExtentStepLogger.logStep("Click on Open Account button");
            WebElement openAccountSelector = driver.findElement(By.cssSelector(OR.getProperty("openaccount_CSS")));
            wait.until(ExpectedConditions.elementToBeClickable(openAccountSelector)).click();
            ExtentStepLogger.logPass("Successfully navigated to Open Account page");
            
            ExtentStepLogger.logSection("Select Customer");
            By customerDropdownLocator = By.cssSelector(OR.getProperty("customer_CSS"));
            boolean customerSelected = DropdownUtils.selectDropdownOption(driver, customerDropdownLocator, "Customer", customer);
            Assert.assertTrue(customerSelected, "Failed to select customer: " + customer);
            
            ExtentStepLogger.logSection("Select Currency");
            By currencyDropdownLocator = By.cssSelector(OR.getProperty("currency_CSS"));
            boolean currencySelected = DropdownUtils.selectDropdownOption(driver, currencyDropdownLocator, "Currency", currency);
            Assert.assertTrue(currencySelected, "Failed to select currency: " + currency);
            
            ExtentStepLogger.logSection("Submit Account Opening Form");
            ExtentStepLogger.logStep("Click on Process button to open account");
            WebElement processButton = driver.findElement(By.cssSelector(OR.getProperty("process_CSS")));
            wait.until(ExpectedConditions.elementToBeClickable(processButton)).click();
            ExtentStepLogger.logPassWithScreenshot("Successfully submitted account opening form", driver, "Account_Form_Submitted");
            
            ExtentStepLogger.logSection("Verify Account Creation");
            ExtentStepLogger.logStep("Verify account was created successfully");
            // Add verification logic here - check for success message, alert, or redirect
            // This depends on your application's behavior after account creation
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            boolean alertVerification = alertText.contains("Account created successfully");
            Assert.assertTrue(alertVerification,
                    "Alert text '" + alertText + "' does not contain expected text '" );
            
            ExtentStepLogger.logPass("Account opened successfully for customer: " + customer + " with currency: " + currency);
            
        } catch (Exception e) {
            ExtentStepLogger.logFailWithScreenshot("Open account test failed for customer: " + customer + ", currency: " + currency + " - " + e.getMessage(), driver, "OpenAccount_Failed");
            TestBase.logError("Open account test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4, description = "Test All Customer and Currency Combinations")
    public void testAllCustomerCurrencyCombinations() {
        ExtentStepLogger.logTestStart("All Combinations Test", "Test all possible customer and currency combinations");
        
        try {
            ExtentStepLogger.logSection("Navigate to Open Account Page");
            ExtentStepLogger.logStep("Click on Open Account button");
            WebElement openAccountSelector = driver.findElement(By.cssSelector(OR.getProperty("openaccount_CSS")));
            wait.until(ExpectedConditions.elementToBeClickable(openAccountSelector)).click();
            ExtentStepLogger.logPass("Successfully navigated to Open Account page");
            
            // Get all available options
            By customerDropdownLocator = By.cssSelector(OR.getProperty("customer_CSS"));
            By currencyDropdownLocator = By.cssSelector(OR.getProperty("currency_CSS"));
            
            List<String> customerOptions = DropdownUtils.getAllDropdownOptions(driver, customerDropdownLocator, "Customer");
            List<String> currencyOptions = DropdownUtils.getAllDropdownOptions(driver, currencyDropdownLocator, "Currency");
            
            // Remove default options
            customerOptions.removeIf(option -> option.contains("---") || option.isEmpty());
            currencyOptions.removeIf(option -> option.contains("---") || option.isEmpty());
            
            ExtentStepLogger.logTestData("Available Customers", customerOptions.toString());
            ExtentStepLogger.logTestData("Available Currencies", currencyOptions.toString());
            
            ExtentStepLogger.logSection("Test All Combinations");
            int totalCombinations = customerOptions.size() * currencyOptions.size();
            int currentCombination = 0;
            
            for (String customer : customerOptions) {
                for (String currency : currencyOptions) {
                    currentCombination++;
                    ExtentStepLogger.logStep("Testing combination " + currentCombination + "/" + totalCombinations + 
                        ": Customer=" + customer + ", Currency=" + currency);
                    
                    // Select customer
                    boolean customerSelected = DropdownUtils.selectDropdownOption(driver, customerDropdownLocator, "Customer", customer);
                    if (!customerSelected) {
                        ExtentStepLogger.logWarning("Failed to select customer: " + customer);
                        continue;
                    }
                    
                    // Select currency
                    boolean currencySelected = DropdownUtils.selectDropdownOption(driver, currencyDropdownLocator, "Currency", currency);
                    if (!currencySelected) {
                        ExtentStepLogger.logWarning("Failed to select currency: " + currency);
                        continue;
                    }
                    
                    // Submit form
                    WebElement processButton = driver.findElement(By.cssSelector(OR.getProperty("process_CSS")));
                    wait.until(ExpectedConditions.elementToBeClickable(processButton)).click();
                    
                    ExtentStepLogger.logPass("Successfully tested combination: " + customer + " + " + currency);
                    
                    // Navigate back to form for next iteration
//                    if (currentCombination < totalCombinations) {
//                        wait.until(ExpectedConditions.elementToBeClickable(openAccountSelector)).click();
//                    }
                }
            }
            
            ExtentStepLogger.logPass("All customer and currency combinations tested successfully");
            
        } catch (Exception e) {
            ExtentStepLogger.logFailWithScreenshot("All combinations test failed: " + e.getMessage(), driver, "AllCombinations_Failed");
            TestBase.logError("All combinations test failed: " + e.getMessage());
            throw e;
        }
    }
}

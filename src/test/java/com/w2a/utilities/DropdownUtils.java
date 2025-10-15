package com.w2a.utilities;

import com.w2a.base.TestBase;
import com.w2a.utilities.ExtentStepLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling dropdown operations and verifications
 * Provides methods to verify dropdown options and select values
 */
public class DropdownUtils {
    
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    
    /**
     * Gets all options from a dropdown
     * @param driver WebDriver instance
     * @param dropdownLocator Locator for the dropdown element
     * @param dropdownName Name of dropdown for logging
     * @return List of option texts
     */
    public static List<String> getAllDropdownOptions(WebDriver driver, By dropdownLocator, String dropdownName) {
        List<String> options = new ArrayList<>();
        try {
            ExtentStepLogger.logStep("Get all options from " + dropdownName + " dropdown");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
            WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            
            Select select = new Select(dropdownElement);
            List<WebElement> optionElements = select.getOptions();
            
            for (WebElement option : optionElements) {
                String optionText = option.getText().trim();
                if (!optionText.isEmpty()) {
                    options.add(optionText);
                }
            }
            
            ExtentStepLogger.logPass("Successfully retrieved " + options.size() + " options from " + dropdownName);
            TestBase.logInfo("Dropdown " + dropdownName + " options: " + options);
            
        } catch (Exception e) {
            ExtentStepLogger.logFail("Failed to get options from " + dropdownName + ": " + e.getMessage());
            TestBase.logError("Error getting dropdown options: " + e.getMessage());
        }
        
        return options;
    }
    
    /**
     * Verifies all expected options are present in dropdown
     * @param driver WebDriver instance
     * @param dropdownLocator Locator for the dropdown element
     * @param dropdownName Name of dropdown for logging
     * @param expectedOptions List of expected option texts
     * @return true if all expected options are found, false otherwise
     */
    public static boolean verifyDropdownOptions(WebDriver driver, By dropdownLocator, String dropdownName, List<String> expectedOptions) {
        try {
            ExtentStepLogger.logStep("Verify all expected options are present in " + dropdownName + " dropdown");
            
            List<String> actualOptions = getAllDropdownOptions(driver, dropdownLocator, dropdownName);
            boolean allOptionsFound = true;
            List<String> missingOptions = new ArrayList<>();
            List<String> extraOptions = new ArrayList<>();
            
            // Check for missing expected options
            for (String expectedOption : expectedOptions) {
                if (!actualOptions.contains(expectedOption)) {
                    missingOptions.add(expectedOption);
                    allOptionsFound = false;
                }
            }
            
            // Check for unexpected options
            for (String actualOption : actualOptions) {
                if (!expectedOptions.contains(actualOption)) {
                    extraOptions.add(actualOption);
                }
            }
            
            if (allOptionsFound && extraOptions.isEmpty()) {
                ExtentStepLogger.logPass("All expected options verified in " + dropdownName + " dropdown");
                ExtentStepLogger.logVerification("Dropdown Options Verification", 
                    "All expected options present", "All expected options present", true);
            } else {
                String verificationMessage = "Missing: " + missingOptions + ", Extra: " + extraOptions;
                ExtentStepLogger.logFail("Dropdown verification failed for " + dropdownName + ": " + verificationMessage);
                ExtentStepLogger.logVerification("Dropdown Options Verification", 
                    expectedOptions.toString(), actualOptions.toString(), false);
            }
            
            return allOptionsFound && extraOptions.isEmpty();
            
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error verifying dropdown options for " + dropdownName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Selects an option from dropdown by visible text
     * @param driver WebDriver instance
     * @param dropdownLocator Locator for the dropdown element
     * @param dropdownName Name of dropdown for logging
     * @param optionText Text of option to select
     * @return true if selection successful, false otherwise
     */
    public static boolean selectDropdownOption(WebDriver driver, By dropdownLocator, String dropdownName, String optionText) {
        try {
            ExtentStepLogger.logStep("Select '" + optionText + "' from " + dropdownName + " dropdown");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
            WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            
            Select select = new Select(dropdownElement);
            select.selectByVisibleText(optionText);
            
            // Verify selection
            String selectedOption = select.getFirstSelectedOption().getText();
            boolean selectionSuccess = selectedOption.equals(optionText);
            
            if (selectionSuccess) {
                ExtentStepLogger.logPass("Successfully selected '" + optionText + "' from " + dropdownName);
                ExtentStepLogger.logVerification("Dropdown Selection", optionText, selectedOption, true);
            } else {
                ExtentStepLogger.logFail("Failed to select '" + optionText + "' from " + dropdownName + ". Selected: " + selectedOption);
                ExtentStepLogger.logVerification("Dropdown Selection", optionText, selectedOption, false);
            }
            
            return selectionSuccess;
            
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error selecting option from " + dropdownName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Selects an option from dropdown by index
     * @param driver WebDriver instance
     * @param dropdownLocator Locator for the dropdown element
     * @param dropdownName Name of dropdown for logging
     * @param index Index of option to select
     * @return true if selection successful, false otherwise
     */
    public static boolean selectDropdownOptionByIndex(WebDriver driver, By dropdownLocator, String dropdownName, int index) {
        try {
            ExtentStepLogger.logStep("Select option at index " + index + " from " + dropdownName + " dropdown");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
            WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            
            Select select = new Select(dropdownElement);
            select.selectByIndex(index);
            
            String selectedOption = select.getFirstSelectedOption().getText();
            ExtentStepLogger.logPass("Successfully selected option at index " + index + ": '" + selectedOption + "'");
            
            return true;
            
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error selecting option by index from " + dropdownName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the currently selected option from dropdown
     * @param driver WebDriver instance
     * @param dropdownLocator Locator for the dropdown element
     * @param dropdownName Name of dropdown for logging
     * @return Currently selected option text
     */
    public static String getSelectedDropdownOption(WebDriver driver, By dropdownLocator, String dropdownName) {
        try {
            ExtentStepLogger.logStep("Get currently selected option from " + dropdownName + " dropdown");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
            WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            
            Select select = new Select(dropdownElement);
            String selectedOption = select.getFirstSelectedOption().getText();
            
            ExtentStepLogger.logPass("Currently selected option in " + dropdownName + ": '" + selectedOption + "'");
            return selectedOption;
            
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error getting selected option from " + dropdownName + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifies dropdown is enabled and clickable
     * @param driver WebDriver instance
     * @param dropdownLocator Locator for the dropdown element
     * @param dropdownName Name of dropdown for logging
     * @return true if dropdown is enabled, false otherwise
     */
    public static boolean verifyDropdownEnabled(WebDriver driver, By dropdownLocator, String dropdownName) {
        try {
            ExtentStepLogger.logStep("Verify " + dropdownName + " dropdown is enabled");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
            WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            
            boolean isEnabled = dropdownElement.isEnabled();
            boolean isDisplayed = dropdownElement.isDisplayed();
            
            if (isEnabled && isDisplayed) {
                ExtentStepLogger.logPass(dropdownName + " dropdown is enabled and displayed");
                ExtentStepLogger.logVerification("Dropdown Enabled", "Enabled", "Enabled", true);
            } else {
                ExtentStepLogger.logFail(dropdownName + " dropdown is not enabled or displayed. Enabled: " + isEnabled + ", Displayed: " + isDisplayed);
                ExtentStepLogger.logVerification("Dropdown Enabled", "Enabled", "Enabled: " + isEnabled + ", Displayed: " + isDisplayed, false);
            }
            
            return isEnabled && isDisplayed;
            
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error verifying dropdown enabled state for " + dropdownName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Comprehensive dropdown verification - checks all aspects
     * @param driver WebDriver instance
     * @param dropdownLocator Locator for the dropdown element
     * @param dropdownName Name of dropdown for logging
     * @param expectedOptions List of expected options
     * @return true if all verifications pass, false otherwise
     */
    public static boolean comprehensiveDropdownVerification(WebDriver driver, By dropdownLocator, String dropdownName, List<String> expectedOptions) {
        ExtentStepLogger.logSection("Comprehensive Dropdown Verification: " + dropdownName);
        
        boolean allPassed = true;
        
        // 1. Verify dropdown is enabled
        boolean enabledCheck = verifyDropdownEnabled(driver, dropdownLocator, dropdownName);
        allPassed = allPassed && enabledCheck;
        
        // 2. Get all options
        List<String> actualOptions = getAllDropdownOptions(driver, dropdownLocator, dropdownName);
        boolean hasOptions = !actualOptions.isEmpty();
        allPassed = allPassed && hasOptions;
        
        if (!hasOptions) {
            ExtentStepLogger.logFail(dropdownName + " dropdown has no options");
            return false;
        }
        
        // 3. Verify expected options
        if (expectedOptions != null && !expectedOptions.isEmpty()) {
            boolean optionsCheck = verifyDropdownOptions(driver, dropdownLocator, dropdownName, expectedOptions);
            allPassed = allPassed && optionsCheck;
        }
        
        // 4. Test selection functionality
        if (!actualOptions.isEmpty()) {
            String firstOption = actualOptions.get(0);
            boolean selectionCheck = selectDropdownOption(driver, dropdownLocator, dropdownName, firstOption);
            allPassed = allPassed && selectionCheck;
        }
        
        ExtentStepLogger.logVerification("Comprehensive Dropdown Verification", "All checks passed", 
            allPassed ? "All checks passed" : "Some checks failed", allPassed);
        
        return allPassed;
    }
}

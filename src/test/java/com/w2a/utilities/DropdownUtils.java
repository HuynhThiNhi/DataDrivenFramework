package com.w2a.utilities;

import com.w2a.base.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for dropdown operations and verifications.
 * Includes By-based helpers and WebElement-based overloads.
 */
public class DropdownUtils {

    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    // -------------------------
    // By-based convenience APIs
    // -------------------------

    public static List<String> getAllDropdownOptions(WebDriver driver, By dropdownLocator, String dropdownName) {
        try {
            WebElement dropdownElement = waitForClickable(driver, dropdownLocator);
            return getAllDropdownOptions(dropdownElement, dropdownName);
        } catch (Exception e) {
            ExtentStepLogger.logFail("Failed to get options from " + dropdownName + ": " + e.getMessage());
            TestBase.logError("Error getting dropdown options: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static boolean verifyDropdownOptions(WebDriver driver, By dropdownLocator, String dropdownName, List<String> expectedOptions) {
        try {
            WebElement dropdownElement = waitForClickable(driver, dropdownLocator);
            return verifyDropdownOptions(dropdownElement, dropdownName, expectedOptions);
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error verifying dropdown options for " + dropdownName + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean selectDropdownOption(WebDriver driver, By dropdownLocator, String dropdownName, String optionText) {
        try {
            WebElement dropdownElement = waitForClickable(driver, dropdownLocator);
            return selectDropdownOption(dropdownElement, dropdownName, optionText);
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error selecting option from " + dropdownName + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean selectDropdownOptionByIndex(WebDriver driver, By dropdownLocator, String dropdownName, int index) {
        try {
            WebElement dropdownElement = waitForClickable(driver, dropdownLocator);
            return selectDropdownOptionByIndex(dropdownElement, dropdownName, index);
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error selecting option by index from " + dropdownName + ": " + e.getMessage());
            return false;
        }
    }

    public static String getSelectedDropdownOption(WebDriver driver, By dropdownLocator, String dropdownName) {
        try {
            WebElement dropdownElement = waitForClickable(driver, dropdownLocator);
            return getSelectedDropdownOption(dropdownElement, dropdownName);
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error getting selected option from " + dropdownName + ": " + e.getMessage());
            return null;
        }
    }

    public static boolean verifyDropdownEnabled(WebDriver driver, By dropdownLocator, String dropdownName) {
        try {
            WebElement dropdownElement = waitForClickable(driver, dropdownLocator);
            return verifyDropdownEnabled(dropdownElement, dropdownName);
        } catch (Exception e) {
            ExtentStepLogger.logFail("Error verifying dropdown enabled state for " + dropdownName + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean comprehensiveDropdownVerification(WebDriver driver, By dropdownLocator, String dropdownName, List<String> expectedOptions) {
        ExtentStepLogger.logSection("Comprehensive Dropdown Verification: " + dropdownName);
        boolean allPassed = true;

        boolean enabled = verifyDropdownEnabled(driver, dropdownLocator, dropdownName);
        allPassed = allPassed && enabled;

        List<String> actualOptions = getAllDropdownOptions(driver, dropdownLocator, dropdownName);
        boolean hasOptions = !actualOptions.isEmpty();
        allPassed = allPassed && hasOptions;
        if (!hasOptions) {
            ExtentStepLogger.logFail(dropdownName + " dropdown has no options");
            return false;
        }

        if (expectedOptions != null && !expectedOptions.isEmpty()) {
            boolean optionsOk = verifyDropdownOptions(driver, dropdownLocator, dropdownName, expectedOptions);
            allPassed = allPassed && optionsOk;
        }

        boolean selectionOk = selectDropdownOption(driver, dropdownLocator, dropdownName, actualOptions.get(0));
        allPassed = allPassed && selectionOk;

        ExtentStepLogger.logVerification("Comprehensive Dropdown Verification", "All checks passed",
                allPassed ? "All checks passed" : "Some checks failed", allPassed);
        return allPassed;
    }

    // ----------------------------------
    // WebElement-based preferred overloads
    // ----------------------------------

    public static List<String> getAllDropdownOptions(WebElement dropdownElement, String dropdownName) {
        List<String> options = new ArrayList<>();
        Select select = new Select(dropdownElement);
        List<WebElement> optionElements = select.getOptions();
        for (WebElement option : optionElements) {
            String text = option.getText().trim();
            if (!text.isEmpty()) {
                options.add(text);
            }
        }
        ExtentStepLogger.logPass("Retrieved " + options.size() + " options from " + dropdownName);
        TestBase.logInfo("Dropdown " + dropdownName + " options: " + options);
        return options;
    }

    public static boolean verifyDropdownOptions(WebElement dropdownElement, String dropdownName, List<String> expectedOptions) {
        List<String> actualOptions = getAllDropdownOptions(dropdownElement, dropdownName);
        boolean allFound = true;
        List<String> missing = new ArrayList<>();
        List<String> extra = new ArrayList<>();

        for (String exp : expectedOptions) {
            if (!actualOptions.contains(exp)) {
                missing.add(exp);
                allFound = false;
            }
        }
        for (String act : actualOptions) {
            if (!expectedOptions.contains(act)) {
                extra.add(act);
            }
        }

        if (allFound && extra.isEmpty()) {
            ExtentStepLogger.logPass("All expected options verified in " + dropdownName);
            ExtentStepLogger.logVerification("Dropdown Options Verification", "All expected options present", "All expected options present", true);
        } else {
            String msg = "Missing: " + missing + ", Extra: " + extra;
            ExtentStepLogger.logFail("Dropdown verification failed for " + dropdownName + ": " + msg);
            ExtentStepLogger.logVerification("Dropdown Options Verification", expectedOptions.toString(), actualOptions.toString(), false);
        }
        return allFound && extra.isEmpty();
    }

    public static boolean selectDropdownOption(WebElement dropdownElement, String dropdownName, String optionText) {
        Select select = new Select(dropdownElement);
        select.selectByVisibleText(optionText);
        String selected = select.getFirstSelectedOption().getText();
        boolean ok = selected.equals(optionText);
        ExtentStepLogger.logVerification("Dropdown Selection", optionText, selected, ok);
        if (ok) {
            ExtentStepLogger.logPass("Selected '" + optionText + "' from " + dropdownName);
        } else {
            ExtentStepLogger.logFail("Failed to select '" + optionText + "' from " + dropdownName + " (selected: " + selected + ")");
        }
        return ok;
    }

    public static boolean selectDropdownOptionByIndex(WebElement dropdownElement, String dropdownName, int index) {
        Select select = new Select(dropdownElement);
        select.selectByIndex(index);
        String selected = select.getFirstSelectedOption().getText();
        ExtentStepLogger.logPass("Selected index " + index + " ('" + selected + "') from " + dropdownName);
        return true;
    }

    public static String getSelectedDropdownOption(WebElement dropdownElement, String dropdownName) {
        Select select = new Select(dropdownElement);
        String selected = select.getFirstSelectedOption().getText();
        ExtentStepLogger.logPass("Currently selected in " + dropdownName + ": '" + selected + "'");
        return selected;
    }

    public static boolean verifyDropdownEnabled(WebElement dropdownElement, String dropdownName) {
        boolean enabled = dropdownElement.isEnabled();
        boolean displayed = dropdownElement.isDisplayed();
        boolean ok = enabled && displayed;
        if (ok) {
            ExtentStepLogger.logPass(dropdownName + " dropdown is enabled and displayed");
            ExtentStepLogger.logVerification("Dropdown Enabled", "Enabled", "Enabled", true);
        } else {
            ExtentStepLogger.logFail(dropdownName + " dropdown is not enabled or displayed. Enabled: " + enabled + ", Displayed: " + displayed);
            ExtentStepLogger.logVerification("Dropdown Enabled", "Enabled", "Enabled: " + enabled + ", Displayed: " + displayed, false);
        }
        return ok;
    }

    // -----------------
    // Internal helpers
    // -----------------

    private static WebElement waitForClickable(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}



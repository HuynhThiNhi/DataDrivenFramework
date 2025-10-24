package com.w2a.testcases;

import com.w2a.base.TestBase;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends TestBase {
    @Test
    public void BankManagerLoginTest() throws InterruptedException {
        long threadId = Thread.currentThread().getId();
        logger.info("Test STARTING on thread: " + threadId);
        logger.info("LoginTest: Start executing BankManagerLoginTest");
        getDriver().findElement(By.cssSelector(OR.getProperty("bmlBtn_CSS"))).click();
        Assert.assertTrue(isElementPresent(By.cssSelector(OR.getProperty("addCustBtn_CSS"))), "Login not successfully");
        Thread.sleep(3000);
    }

}

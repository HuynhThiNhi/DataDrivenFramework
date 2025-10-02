package com.w2a.testcases;

import com.w2a.base.TestBase;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class LoginTest extends TestBase {
    @Test
    public void BankManagerLoginTest() throws InterruptedException {
        logger.info("LoginTest: Start executing BankManagerLoginTest");
        System.out.println(OR.getProperty("bmlBtn_CSS"));
        driver.findElement(By.cssSelector(OR.getProperty("bmlBtn_CSS"))).click();
        Thread.sleep(3000);
    }

}

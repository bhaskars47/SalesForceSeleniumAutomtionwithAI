package com.salesforce.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.salesforce.utils.WaitUtils;
import com.salesforce.utils.LoggerUtils;

public class BasePage {

    protected void click(By locator, String elementName) {
        try {
            WaitUtils.waitForClickability(locator).click();
            LoggerUtils.info("Clicked on: " + elementName);
        } catch (Exception e) {
            LoggerUtils.error("Failed to click on: " + elementName);
            throw e;
        }
    }

    protected void sendKeys(By locator, String text, String elementName) {
        try {
            WebElement element = WaitUtils.waitForVisibility(locator);
            element.clear();
            element.sendKeys(text);
            LoggerUtils.info("Entered text in: " + elementName);
        } catch (Exception e) {
            LoggerUtils.error("Failed to enter text in: " + elementName);
            throw e;
        }
    }

    protected String getTitle() {
        return com.salesforce.driver.DriverFactory.getDriver().getTitle();
    }

    protected boolean isDisplayed(By locator, String elementName) {
        try {
            return WaitUtils.waitForVisibility(locator).isDisplayed();
        } catch (Exception e) {
            LoggerUtils.error("Element not displayed: " + elementName);
            return false;
        }
    }
}

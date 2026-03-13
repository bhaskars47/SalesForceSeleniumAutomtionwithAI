package com.salesforce.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.salesforce.utils.WaitUtils;
import com.salesforce.utils.LoggerUtils;
import com.salesforce.utils.rag.SelfHealingUtils;
import org.openqa.selenium.TimeoutException;

public class BasePage {

    protected void click(By locator, String elementName) {
        try {
            WaitUtils.waitForClickability(locator).click();
            LoggerUtils.info("Clicked on: " + elementName);
        } catch (TimeoutException e) {
            LoggerUtils.error("Failed to click on: " + elementName + ". Attempting Self-Healing...");
            LoggerUtils.info("Calling getPageSource()...");
            String pageSource = com.salesforce.driver.DriverFactory.getDriver().getPageSource();
            LoggerUtils.info("getPageSource() completed. Calling healLocator()...");
            By newLocator = SelfHealingUtils.healLocator(elementName, locator, pageSource);
            LoggerUtils.info("healLocator() returned: " + newLocator);

            if (newLocator != null) {
                LoggerUtils.info("Retrying click with new locator: " + newLocator);
                WaitUtils.waitForClickability(newLocator).click();
                LoggerUtils.info("Self-Healing successful! Clicked on: " + elementName);
            } else {
                throw e; // Throw original if healing fails
            }
        }
    }

    protected void sendKeys(By locator, String text, String elementName) {
        try {
            WebElement element = WaitUtils.waitForVisibility(locator);
            element.clear();
            element.sendKeys(text);
            LoggerUtils.info("Entered text in: " + elementName);
        } catch (TimeoutException e) {
            LoggerUtils.error("Failed to enter text in: " + elementName + ". Attempting Self-Healing...");
            LoggerUtils.info("Calling getPageSource()...");
            String pageSource = com.salesforce.driver.DriverFactory.getDriver().getPageSource();
            LoggerUtils.info("getPageSource() completed. Calling healLocator()...");
            By newLocator = SelfHealingUtils.healLocator(elementName, locator, pageSource);
            LoggerUtils.info("healLocator() returned: " + newLocator);

            if (newLocator != null) {
                LoggerUtils.info("Retrying sendKeys with new locator: " + newLocator);
                WebElement element = WaitUtils.waitForVisibility(newLocator);
                element.clear();
                element.sendKeys(text);
                LoggerUtils.info("Self-Healing successful! Entered text in: " + elementName);
            } else {
                throw e;
            }
        }
    }

    protected String getTitle() {
        return com.salesforce.driver.DriverFactory.getDriver().getTitle();
    }

    protected boolean isDisplayed(By locator, String elementName) {
        try {
            return WaitUtils.waitForVisibility(locator).isDisplayed();
        } catch (TimeoutException e) {
            LoggerUtils.error("Element not displayed: " + elementName + ". Attempting Self-Healing...");
            LoggerUtils.info("Calling getPageSource()...");
            String pageSource = com.salesforce.driver.DriverFactory.getDriver().getPageSource();
            LoggerUtils.info("getPageSource() completed. Calling healLocator()...");
            By newLocator = SelfHealingUtils.healLocator(elementName, locator, pageSource);
            LoggerUtils.info("healLocator() returned: " + newLocator);

            return newLocator != null && WaitUtils.waitForVisibility(newLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

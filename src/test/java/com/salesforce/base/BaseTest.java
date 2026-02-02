package com.salesforce.base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import com.salesforce.driver.DriverFactory;
import com.salesforce.utils.ConfigReader;
import com.salesforce.utils.LoggerUtils;

public class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        LoggerUtils.info("Starting Test Setup...");
        DriverFactory.initDriver();
        DriverFactory.getDriver().get(ConfigReader.get("url"));
        LoggerUtils.info("Navigated to URL: " + ConfigReader.get("url"));
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        LoggerUtils.info("Tearing down test...");
        DriverFactory.quitDriver();
    }
}

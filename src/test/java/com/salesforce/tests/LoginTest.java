package com.salesforce.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.salesforce.base.BaseTest;
import com.salesforce.pages.LoginPage;
import com.salesforce.utils.ConfigReader;

public class LoginTest extends BaseTest {

    @Test(groups = { "smoke", "regression" })
    public void loginTest() {
        LoginPage loginPage = new LoginPage();

        // Assert Title
        String title = loginPage.getLoginPageTitle();
        Assert.assertTrue(title.contains("Salesforce"), "Title does not contain Salesforce");

        // Perform Login
        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));

        // Check for error (since credentials might be fake)
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed for invalid credentials");
    }
}

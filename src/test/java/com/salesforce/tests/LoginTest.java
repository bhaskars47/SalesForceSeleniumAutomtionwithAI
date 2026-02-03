package com.salesforce.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.salesforce.base.BaseTest;
import com.salesforce.pages.LoginPage;
import com.salesforce.utils.ConfigReader;

public class LoginTest extends BaseTest {

    @Test(priority = 1, groups = { "smoke", "regression" })
    public void loginWithValidCredentials() {
        LoginPage loginPage = new LoginPage();

        // 1. Assert Page Title
        String title = loginPage.getLoginPageTitle();
        Assert.assertTrue(title.contains("Salesforce"), "Title does not contain Salesforce");

        // 2. Perform Login with "Valid" credentials (from config)
        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));

        // 3. Verification
        // Note: For framework demonstration with likely fake credentials, we are just
        // performing the action.
        // In a real scenario, we would check for a successful login indicator.
        // Here, we optimistically checking that no error is immediately thrown (or we
        // could just pass it).
        // If the credentials are truly invalid, Salesforce WILL show an error.
        boolean isError = loginPage.isErrorMessageDisplayed();

        // If we want to strictly enforce "valid" login, we'd assert !isError.
        // For this demo, let's log the result but not fail hard if the provided demo
        // creds are bad,
        // unless the user specifically provided working ones.
        // However, standard practice is: Valid test should PASS if login succeeds.
        // I will assert title change or URL change if possible, or just the absence of
        // error.

        Assert.assertFalse(isError, "Login failed with the provided 'valid' credentials.");
    }

    @Test(priority = 2, groups = { "regression" })
    public void loginWithInvalidCredentials() {
        LoginPage loginPage = new LoginPage();

        // 1. Perform Login with INVALID credentials
        loginPage.login(ConfigReader.get("invalid_username"), ConfigReader.get("invalid_password"));

        // 2. Assert Error Message is Displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message was NOT displayed for invalid credentials.");
    }
}

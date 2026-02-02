package com.salesforce.pages;

import org.openqa.selenium.By;
import com.salesforce.base.BasePage;

public class LoginPage extends BasePage {

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("Login");
    private final By errorMessage = By.id("error");

    public void login(String username, String password) {
        sendKeys(usernameField, username, "Username Field");
        sendKeys(passwordField, password, "Password Field");
        click(loginButton, "Login Button");
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage, "Error Message");
    }

    public String getLoginPageTitle() {
        return getTitle();
    }
}

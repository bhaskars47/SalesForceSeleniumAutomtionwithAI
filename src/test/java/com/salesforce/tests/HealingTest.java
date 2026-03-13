package com.salesforce.tests;

import com.salesforce.utils.rag.SelfHealingUtils;
import org.openqa.selenium.By;

public class HealingTest {
    public static void main(String[] args) {
        String mockDom = "<html><body><form id='login_form'><input type='email' id='username' class='input r4 wide mb16 mt8 username' name='username' aria-describedby='error' style='display: block;'><input type='password' id='password'></form></body></html>";
        By brokenLocator = By.cssSelector(".username_BROKEN");

        System.out.println("Starting Healing Test...");
        By result = SelfHealingUtils.healLocator("Username Field", brokenLocator, mockDom);
        System.out.println("Result: " + result);
    }
}

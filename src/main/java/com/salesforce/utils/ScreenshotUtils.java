package com.salesforce.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.salesforce.driver.DriverFactory;

public class ScreenshotUtils {

    public static String getScreenshot(String testName) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null)
            return null;

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        // Use the centralized dynamic path from ExtentReportManager
        String destinationPath = ExtentReportManager.getScreenshotPath() + testName + ".png";

        try {
            FileUtils.copyFile(src, new File(destinationPath));
            return destinationPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBase64Image() {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null)
            return null;
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }
}

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
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String path = System.getProperty("user.dir") + "/reports/screenshots/" + testName + "_" + timestamp + ".png";

        try {
            FileUtils.copyFile(src, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String getBase64Image() {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null)
            return null;
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }
}

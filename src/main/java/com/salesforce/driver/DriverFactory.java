package com.salesforce.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.salesforce.utils.ConfigReader;
import com.salesforce.utils.LoggerUtils;
import java.time.Duration;
import com.salesforce.constants.FrameworkConstants;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void initDriver() {
        if (getDriver() == null) {
            String browser = ConfigReader.get("browser");
            String headless = ConfigReader.get("headless");

            LoggerUtils.info("Initializing driver for browser: " + browser);

            switch (browser.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    if ("true".equalsIgnoreCase(headless)) {
                        options.addArguments("--headless");
                    }
                    driver.set(new ChromeDriver(options));
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver.set(new FirefoxDriver());
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver.set(new EdgeDriver());
                    break;
                default:
                    throw new RuntimeException("Browser not supported: " + browser);
            }

            getDriver().manage().window().maximize();
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(FrameworkConstants.IMPLICIT_WAIT));
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (getDriver() != null) {
            LoggerUtils.info("Quitting driver");
            getDriver().quit();
            driver.remove();
        }
    }
}

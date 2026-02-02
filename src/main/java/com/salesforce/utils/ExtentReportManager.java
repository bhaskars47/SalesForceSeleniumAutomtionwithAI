package com.salesforce.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.salesforce.constants.FrameworkConstants;

/**
 * Manages the initialization and configuration of Extent Reports.
 * Creates a timestamped folder for each execution to ensure reports are not
 * overwritten.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static String reportPath;
    private static String screenshotPath;

    private ExtentReportManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initializes the Extent Report if not already initialized.
     * Generates a timestamp once per execution and creates the report directory
     * structure.
     * 
     * @return Single instance of ExtentReports
     */
    public static ExtentReports getExtentReports() {
        if (extent == null) {
            // 1. Generate Timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String timestamp = now.format(formatter);

            // 2. Define Paths
            String baseReportDir = FrameworkConstants.EXTENT_REPORT_FOLDER_PATH + timestamp;
            reportPath = baseReportDir + "/TestExecutionReport_" + timestamp + ".html";
            screenshotPath = baseReportDir + "/screenshots/";

            // 3. Create Directories
            File outputDir = new File(baseReportDir);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File screenDir = new File(screenshotPath);
            if (!screenDir.exists()) {
                screenDir.mkdirs();
            }

            // 4. Configure Reporter
            ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
            reporter.config().setReportName("Salesforce Automation Execution");
            reporter.config().setDocumentTitle("Test Execution Report - " + timestamp);
            reporter.config().setTheme(Theme.STANDARD);
            reporter.config().setEncoding("utf-8");

            // 5. Initialize ExtentReports
            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Environment",
                    ConfigReader.get("env") != null ? ConfigReader.get("env").toUpperCase() : "QA");
            extent.setSystemInfo("Execution Start Time", timestamp);
            extent.setSystemInfo("Browser",
                    ConfigReader.get("browser") != null ? ConfigReader.get("browser") : "Chrome");
        }
        return extent;
    }

    /**
     * Returns the dynamic screenshot path provided by the report manager.
     * 
     * @return Absolute path to the screenshots folder
     */
    public static String getScreenshotPath() {
        if (screenshotPath == null) {
            // Trigger initialization if accessed before getExtentReports (unlikely but
            // safe)
            getExtentReports();
        }
        return screenshotPath;
    }
}

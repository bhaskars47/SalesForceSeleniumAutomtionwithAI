package com.salesforce.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.salesforce.constants.FrameworkConstants;
import com.salesforce.utils.LoggerUtils;
import com.salesforce.utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(
                    FrameworkConstants.EXTENT_REPORT_FOLDER_PATH + FrameworkConstants.EXTENT_REPORT_NAME);
            htmlReporter.config().setDocumentTitle("Automation Test Report");
            htmlReporter.config().setReportName("Salesforce Automation Report");

            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
        LoggerUtils.info("Test Suite Started: " + context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
        LoggerUtils.info("Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed");
        LoggerUtils.info("Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "Test Failed: " + result.getThrowable().getMessage());
        String screenshotPath = ScreenshotUtils.getScreenshot(result.getMethod().getMethodName());
        if (screenshotPath != null) {
            test.get().addScreenCaptureFromPath(screenshotPath);
        }
        LoggerUtils.error("Test Failed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test Skipped");
        LoggerUtils.warn("Test Skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
        LoggerUtils.info("Test Suite Finished: " + context.getName());
    }
}

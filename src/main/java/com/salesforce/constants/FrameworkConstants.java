package com.salesforce.constants;

public class FrameworkConstants {

    private FrameworkConstants() {
    }

    public static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/config/";
    public static final int EXPLICIT_WAIT = 10;
    public static final int IMPLICIT_WAIT = 10;

    public static final String EXTENT_REPORT_FOLDER_PATH = System.getProperty("user.dir") + "/reports/";
    public static final String EXTENT_REPORT_NAME = "Automation_Report.html";
}

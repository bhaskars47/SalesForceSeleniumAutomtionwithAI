package com.salesforce.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.salesforce.constants.FrameworkConstants;

public class ConfigReader {
    private static Properties properties;

    static {
        try {
            String env = System.getProperty("env");
            if (env == null || env.isEmpty()) {
                env = "qa"; // default environment
            }

            FileInputStream fileInputStream = new FileInputStream(
                    FrameworkConstants.CONFIG_FILE_PATH + env + ".properties");
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configuration file.");
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}

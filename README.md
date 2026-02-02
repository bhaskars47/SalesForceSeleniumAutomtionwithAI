# 🚀 Salesforce Automation Framework

![Java](https://img.shields.io/badge/Java-16%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Selenium](https://img.shields.io/badge/Selenium-4.x-43B02A?style=for-the-badge&logo=selenium&logoColor=white)
![TestNG](https://img.shields.io/badge/TestNG-7.x-FF7F00?style=for-the-badge&logo=testng&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![ExtentReports](https://img.shields.io/badge/Reporting-ExtentReports-blue?style=for-the-badge)

> **An enterprise-grade, scalable test automation framework for Salesforce.**
> Built with a focus on modularity, thread-safety, and maintainability using the Page Object Model (POM) design pattern.

---

## 🏗️ Architecture Design

The framework leverages a **Layered Architecture** to decouple test logic from implementation details.

```mermaid
graph TD
    classDef core fill:#e1f5fe,stroke:#01579b,stroke-width:2px;
    classDef page fill:#fff3e0,stroke:#e65100,stroke-width:2px;
    classDef test fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px;
    classDef util fill:#f3e5f5,stroke:#4a148c,stroke-width:2px;

    subgraph Tests [Test Layer]
        LoginTest:::test --> BaseTest:::test
    end

    subgraph Pages [Page Layer]
        LoginPage:::page --> BasePage:::page
    end

    subgraph Core [Core Layer]
        BaseTest --> DriverFactory:::core
        BasePage --> DriverFactory
    end

    subgraph Utils [Utilities Layer]
        BaseTest --> ConfigReader:::util
        BaseTest --> LoggerUtils:::util
        BasePage --> WaitUtils:::util
        TestListener:::util --> ScreenshotUtils:::util
        TestListener --> LoggerUtils
    end

    TestNG_XML --> TestListener
    TestNG_XML --> LoginTest
```

---

## 📂 Project Structure

Verified, standardized directory layout ensuring separation of concerns.

| Directory | Package | Description |
|-----------|---------|-------------|
| 🛣️ `driver` | `com.salesforce.driver` | **DriverFactory**: Thread-safe (ThreadLocal) WebDriver management. |
| 🧱 `base` | `com.salesforce.base` | **BasePage**: Wraps Selenium actions.<br>**BaseTest**: Manages test lifecycle (setup/teardown). |
| 📄 `pages` | `com.salesforce.pages` | **Page Objects**: Contains locators and page-specific logic (e.g., `LoginPage`). |
| 🛠️ `utils` | `com.salesforce.utils` | **ConfigReader**: Enivronment properties loader.<br>**ExtentReportManager**: Timestamped report generation.<br>**WaitUtils**: Explicit wait helpers.<br>**LoggerUtils**: Log4j2 wrapper.<br>**ScreenshotUtils**: Failure capture. |
| 🧪 `tests` | `com.salesforce.tests` | **Test Classes**: Actual TestNG test scenarios. |
| ⚙️ `resources`| `src/main/resources` | **Config**: `qa.properties`, `uat.properties`...<br>**Suites**: `testng.xml`. |

---

## ⚡ Key Features

- **🧵 Parallel Execution**: Usage of `ThreadLocal<WebDriver>` ensures safe parallel test execution across multiple threads.
- **🌍 Multi-Environment Support**: Seamlessly switch between **QA**, **UAT**, and **PROD** by changing a single flag.
- **🛡️ Robust Synchronization**: No basic `Thread.sleep()`. All interactions use `WaitUtils` for reliable explicit waits.
- **📊 Rich Reporting**: Integrated **ExtentReports** with automatic screenshot attachment on failure.
- **🕒 Preservation**: Reports are saved in timestamped folders (e.g., `reports/2026-02-02_23-00-00/`) to prevent overwriting history.
- **📝 Detailed Logging**: Context-aware logging using Log4j2 for easier debugging.

---

## 🚀 How to Run

### 1️⃣ Default Run (QA Environment)
Executes all tests specified in `testng.xml` against the QA environment.
```bash
mvn clean test
```

### 2️⃣ Environment Specific Run
Run tests against a specific environment (e.g., UAT or PROD).
```bash
mvn clean test -Denv=uat
# or
mvn clean test -Denv=prod
```

### 3️⃣ View Reports
After execution, robust HTML reports are generated in a timestamped folder:
- 📂 **Path**: `reports/yyyy-MM-dd_HH-mm-ss/TestExecutionReport_....html`

---

## 🛠️ Technology Stack

- **Language**: Java 16+
- **Web Automation**: Selenium WebDriver 4.40.0
- **Test Runner**: TestNG 7.12.0
- **Build Tool**: Maven
- **Reporting**: ExtentReports 5.1.1
- **Logging**: Log4j2

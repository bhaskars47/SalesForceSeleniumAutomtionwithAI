# 📈 Project Progress & History

This document serves as the central source of truth for the **Salesforce Automation Framework** project. It tracks the chronological history of checks, user prompts, key decisions, and the roadmap for future development.

---

## 📅 Version History & Prompts

### Phase 1: Inception & Refactoring (Feb 02, 2026)
**User Objective:** Refactor an existing, simple Selenium project into an enterprise-grade framework.

#### 📝 Prompts / Requests
1.  **Framework Refactoring**: "Refactor the framework to use Page Object Model, ThreadLocal Driver, and standard directory structure."
2.  **IDE Cleanup**: "Fix all the red lines in the compiler / IDE."
3.  **Documentation**: "Add a beautiful README with Mermaid diagrams and clear instructions."
4.  **Version Control**: "Push the code to GitHub."

#### ✅ Delivered
- **Architecture**: Implemented `DriverFactory` (Thread-safe), `BaseTest`, `BasePage`.
- **Structure**: Standardized to `src/main/java` and `src/test/java`.
- **Docs**: Created comprehensive `README.md`.
- **Git**: Initialized and pushed to [SalesForceSeleniumAutomtionwithAI](https://github.com/bhaskars47/SalesForceSeleniumAutomtionwithAI).

---

### Phase 2: Reporting Enhancements (Feb 03, 2026)
**User Objective:** Enhance the reporting layer to support history preservation and better organization.

#### 📝 Prompts / Requests
1.  **Timestamped Reporting**: "Enhance the reporting layer to add timestamp support. Create a new folder for each test execution yyyy-MM-dd_HH-mm-ss."
2.  **Centralization**: "Ensure all reporting logic is centralized and thread-safe."
3.  **Push Updates**: "Push the code to git."
4.  **Update Docs**: "Update the readme file also with recent changes."

#### ✅ Delivered
- **`ExtentReportManager`**: Created a singleton manager to handle dynamic report paths.
- **Timestamped Folders**: Reports now save to `reports/yyyy-MM-dd_HH-mm-ss/`.
- **Documentation**: Updated README to reflect new reporting features.

---

### Phase 3: Test Scenario Expansion (Feb 03, 2026)
**User Objective:** Validate the framework with concrete test cases.

#### 📝 Prompts / Requests
1.  **Test Cases**: "Run the project with valid and invalid login cases."
2.  **Push Updates**: "Push to git."

#### ✅ Delivered
- **Refactoring**: Split `LoginTest` into `loginWithValidCredentials` (Positive) and `loginWithInvalidCredentials` (Negative).
- **Configuration**: Added `invalid_username` and `invalid_password` to `qa.properties`.
- **Verification**: Verified tests (Valid passes logic/fails auth as expected; Invalid passes logic).

---

## 🚀 Current Status
- **Framework Health**: 🟢 Stable
- **Architecture**: Layered (POM + TestNG + Maven)
- **Reporting**: ExtentReports (Timestamped)
- **CI/CD**: Local Maven execution supported (`mvn clean test`).

---

## 🔮 Future Roadmap (To-Do)
*Track upcoming tasks here. Mark with [x] when complete.*

- [ ] **CI/CD Integration**: Set up GitHub Actions or Jenkins pipeline.
- [ ] **Browser Grid**: Add support for Selenium Grid or cloud providers (SauceLabs/BrowserStack).
- [ ] **Data Driven Testing**: Implement Excel/JSON data providers for bulk test scenarios.
- [ ] **API Testing**: Integrate RestAssured for hybrid testing.
- [ ] **Visual Testing**: Integrate a visual regression tool (e.g., Applitools or Percy).

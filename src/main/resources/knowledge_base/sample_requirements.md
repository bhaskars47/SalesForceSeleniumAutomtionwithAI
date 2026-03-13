# Salesforce Login Requirements

## Environments
* QA Environment: `https://test.salesforce.com` (sandbox)
* UAT Environment: `https://uat.salesforce.com`
* Production: `https://login.salesforce.com`

## User Accounts
* Standard User: `standard_user@demo.com`
* Admin User: `admin_user@demo.com`
* Locked User: `locked_user@demo.com`

## Validation Rules
1. **Empty Fields:** If a user submits the login form without a password, display the error message: "Please enter your password."
2. **Invalid Credentials:** If a user inputs an incorrect username or password, display the error message: "Please check your username and password. If you still can't log in, contact your Salesforce administrator."
3. **Successful Login:** After a successful login, the user should be redirected to the Home page, and the page title should contain "Home | Salesforce".

## Expected Elements
* Username field ID: `username`
* Password field ID: `password`
* Login Button ID: `Login`

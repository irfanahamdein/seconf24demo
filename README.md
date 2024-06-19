# Automation Testing with Selenium, Cucumber, and OWASP ZAP

This project is a Selenium and Cucumber-based test automation framework for the OWASP Juice Shop application. It includes integration with OWASP ZAP for security testing.

## Project Structure

```
cucumber-selenium-gradle/
|-- build.gradle
|-- src/
    |-- main/
    |   |-- java/
    |       |-- pages/
    |           |-- JuiceShopPage.java
    |-- test/
        |-- java/
            |-- steps/
                |-- JuiceShopLoginSteps.java
                |-- Hooks.java
            |-- runners/
                |-- TestRunner.java
            |-- features/
                |-- juice_shop_login.feature
            |-- resources/
                |-- environment.properties
|-- README.md
```

## Prerequisites

- Java JDK 8 or higher
- Gradle
- Chrome Browser
- ChromeDriver
- OWASP ZAP (Zed Attack Proxy)

## Setup

1. **Clone the repository:**

   ```sh
   git clone <repository-url>
   cd cucumber-selenium-gradle
   ```

2. **Install dependencies:**

   Gradle will automatically download the required dependencies.

3. **Configure environment properties:**

   Edit the `environment.properties` file located in `src/test/resources` to specify your environment configurations.

   ```properties
   # environment.properties

   env=prod

   # Production environment
   prod.url=https://demo.owasp-juice.shop
   prod.username=demo@example.com
   prod.password=password

   # Development environment
   dev.url=http://localhost:3000
   dev.username=dev@example.com
   dev.password=devpassword
   ```

## Running the Tests

### Running without ZAP

To run the tests without OWASP ZAP:

```sh
./gradlew test -Dscan=false
```

### Running with Passive Scanning

To run the tests with OWASP ZAP passive scanning:

```sh
./gradlew test -Dscan=passive
```

### Running with Active Scanning

To run the tests with OWASP ZAP active scanning:

```sh
./gradlew test -Dscan=active
```

### Running in Different Environments

By default, the tests run against the production environment specified in `environment.properties`. To run the tests in a different environment (e.g., development):

```sh
./gradlew test -Dscan=passive -Denv=dev
```

## Project Components

### `build.gradle`

The build configuration file for Gradle, including dependencies for Selenium, Cucumber, WebDriverManager, and OWASP ZAP.

### `JuiceShopPage.java`

This class contains methods to interact with the Juice Shop application, such as opening the app, closing the welcome popup, logging in, and verifying the login status.

### `JuiceShopLoginSteps.java`

This class contains the Cucumber step definitions for the login feature.

### `Hooks.java`

This class sets up and tears down the WebDriver instance before and after each test. It also integrates OWASP ZAP for security testing.

### `TestRunner.java`

This class is the Cucumber test runner.

### `juice_shop_login.feature`

The Cucumber feature file that contains the login scenario.

### `environment.properties`

This file contains environment-specific configurations such as URLs, usernames, and passwords.

## Reports

OWASP ZAP scan reports will be generated in the project root directory:

- `passive-scan-report.html` for passive scans
- `active-scan-report.html` for active scans

## Best Practices

- **Environment Configuration:** Use the `environment.properties` file to manage different environments.
- **Page Object Model (POM):** Organize web elements and actions in page classes for maintainability.
- **Reusable Methods:** Create reusable methods in page classes for common actions.
- **Separation of Concerns:** Keep the test logic in step definition classes and the application-specific interactions in page classes.
- **Security Testing Integration:** Use OWASP ZAP for passive and active security scanning.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License.

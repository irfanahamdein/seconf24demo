package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class JuiceShopPage {
    WebDriver driver;
    WebDriverWait wait;
    Properties properties;

    By dismissButton = By.cssSelector("button[aria-label='Close Welcome Banner']");

    By emailField = By.id("email");
    By passwordField = By.id("password");
    By loginButton = By.id("loginButton");

    public JuiceShopPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.properties = loadProperties();
        PageFactory.initElements(driver, this);
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("environment.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find environment.properties");
                return null;
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props;
    }

    private String getProperty(String key) {
        String env = properties.getProperty("env", "prod"); // Default to prod if not specified
        return properties.getProperty(env + "." + key);
    }

    public void open() {
        String url = getProperty("url");
        driver.get(url);
    }

    public void closeWelcomePopup() {
        WebElement dismissButtonElement = wait.until(ExpectedConditions.elementToBeClickable(dismissButton));
        dismissButtonElement.click();
    }

    public void goToLoginPage() {
        String url = getProperty("url") + "/#/login";
        driver.get(url);
    }

    public void login() {
        String email = getProperty("username");
        String password = getProperty("password");

        WebElement emailFieldElement = wait.until(ExpectedConditions.elementToBeClickable(emailField));
        WebElement passwordFieldElement = wait.until(ExpectedConditions.elementToBeClickable(passwordField));

        emailFieldElement.sendKeys(email);
        passwordFieldElement.sendKeys(password);

        WebElement loginButtonElement = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButtonElement.click();

        // Wait for 2 seconds after login
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        // Check for an element that is only visible after logging in
        // For example, a logout button or a user profile icon
        try {
            WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[aria-label='Show the shopping cart']"))); // Adjust the ID or locator as needed
            return logoutButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

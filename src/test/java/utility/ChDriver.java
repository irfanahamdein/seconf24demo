package utility;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChDriver {
    private static WebDriver driver;
    private static boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "true"));

    public static void startChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        if (isHeadless) {
            options.addArguments("--headless");
        }

        driver = new ChromeDriver(options);
    }

    public static void stopChromeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }
}

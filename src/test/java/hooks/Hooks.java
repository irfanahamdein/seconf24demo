package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import pages.JuiceShopPage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Hooks {

    public static WebDriver driver;
    private static final String ZAP_ADDRESS = "localhost";
    private static final int ZAP_PORT = 8090;
    private static final String ZAP_API_KEY = "changeme"; // Change this to your actual ZAP API key
    private static ClientApi api = new ClientApi(ZAP_ADDRESS, ZAP_PORT, ZAP_API_KEY);
    private String scanType = System.getProperty("scan");
    private boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "true"));
    private JuiceShopPage juiceShopPage;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("browser.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find browser.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String browser = System.getProperty("browser", properties.getProperty("browser", "chrome"));

        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (isHeadless) {
                    firefoxOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (isHeadless) {
                    edgeOptions.addArguments("--headless");
                }
                driver = new EdgeDriver(edgeOptions);
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();

                if (!"false".equals(scanType)) {
                    Proxy proxy = new Proxy();
                    proxy.setHttpProxy(ZAP_ADDRESS + ":" + ZAP_PORT);
                    proxy.setSslProxy(ZAP_ADDRESS + ":" + ZAP_PORT);
                    chromeOptions.setCapability(CapabilityType.PROXY, proxy);
                }

                if (isHeadless) {
                    chromeOptions.addArguments("--headless");
                }

                driver = new ChromeDriver(chromeOptions);
                break;
        }

        juiceShopPage = new JuiceShopPage(driver);

        if ("active".equals(scanType)) {
            try {
                api.core.newSession("", "", "");
            } catch (ClientApiException e) {
                e.printStackTrace();
            }
        }
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }

        if ("passive".equals(scanType)) {
            generateZapReport("passive-scan-report.html");
        } else if ("active".equals(scanType)) {
            performActiveScan(juiceShopPage.getTargetUrl());
            generateZapReport("active-scan-report.html");
        }
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        String currentUrl = driver.getCurrentUrl();

        try {
            if ("passive".equals(scanType)) {
                api.pscan.enableAllScanners();
                api.pscan.setEnabled("true");
            } else if ("active".equals(scanType)) {
                performActiveScan(currentUrl);
            }
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    private void performActiveScan(String targetUrl) {
        try {
            ApiResponse resp = api.ascan.scan(targetUrl, "True", "False", null, null, null);

            String scanId = ((ApiResponseElement) resp).getValue();

            int progress;
            do {
                progress = Integer.parseInt(((ApiResponseElement) api.ascan.status(scanId)).getValue());
                System.out.println("Scan progress : " + progress + "%");
                Thread.sleep(1000);
            } while (progress < 100);
        } catch (ClientApiException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void generateZapReport(String fileName) {
        try {
            byte[] report = api.core.htmlreport();
            Files.write(Paths.get(fileName), report);
        } catch (ClientApiException | IOException e) {
            e.printStackTrace();
        }
    }
}
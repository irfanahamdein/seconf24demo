package steps;

import utility.ChDriver;
import utility.BaseSecurity;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.zaproxy.clientapi.core.ClientApiException;
import pages.JuiceShopPage;

import java.io.IOException;

public class Hooks {

    private WebDriver driver;
    private JuiceShopPage juiceShopPage;

    @Before
    public void setUp() {
        ChDriver.startChromeDriver();
        driver = ChDriver.getDriver();
        juiceShopPage = new JuiceShopPage(driver);
    }

    @After
    public void tearDown() {
        try {
            String currentUrl = driver.getCurrentUrl();
            BaseSecurity.checkRiskCount(currentUrl);
            BaseSecurity.generateScanReport();
        } catch (ClientApiException | IOException e) {
            e.printStackTrace();
        } finally {
            ChDriver.stopChromeDriver();
        }
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        String currentUrl = driver.getCurrentUrl();
        try {
            if ("passive".equals(System.getProperty("scan"))) {
                BaseSecurity.waitForPassiveScanToComplete();
            } else if ("active".equals(System.getProperty("scan"))) {
                BaseSecurity.activeScan(currentUrl);
            }
        } catch (ClientApiException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

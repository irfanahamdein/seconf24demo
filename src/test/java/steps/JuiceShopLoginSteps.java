package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import pages.JuiceShopPage;

import static org.junit.Assert.assertTrue;

public class JuiceShopLoginSteps {
    WebDriver driver = steps.Hooks.driver;
    JuiceShopPage juiceShopPage = new JuiceShopPage(driver);

    @Given("I open Juice Shop")
    public void iOpenJuiceShop() {
        juiceShopPage.open();
        juiceShopPage.closeWelcomePopup();
    }

    @When("I log in")
    public void iLogIn() {
        juiceShopPage.goToLoginPage();
        juiceShopPage.login();
    }

    @Then("I should be logged in")
    public void iShouldBeLoggedIn() {
        assertTrue(juiceShopPage.isLoggedIn());
    }
}

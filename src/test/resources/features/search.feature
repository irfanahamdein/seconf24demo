Feature: Juice Shop Login

  Scenario: Login to Juice Shop
    Given I open Juice Shop
    When I log in
    Then I should be logged in

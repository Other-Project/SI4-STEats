Feature: Restaurant

  Background:
    Given A restaurant named "Restaurant"

  Scenario: Add a new menu item
    When "Restaurant" add a new menu item
    Then the menu item is added to the menu

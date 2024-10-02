Feature: Restaurant

  Background:
    Given A restaurant named "Restaurant"

  Scenario: Add a new menu item
    Given a menuItem named "Boeuf Bourguignon" with a price of 25
    When "Restaurant" add "Boeuf Bourguignon"
    Then "Boeuf Bourguignon" is added to the menu

Feature: Order

  Background:
    Given an user of id "140403"
    Given a restaurant named "La Cafet"

  Scenario: Creating an order
    When the user creates an order and specifies a date, an address and a restaurant
    Then the user can order

  Scenario: Adding items to the cart
    Given the order the user created
    When the user orders "Boeuf Bourguignon" and "Pavé de saumon" from the given restaurant
    Then the items are added to his cart

  Scenario: Remove item from the cart
    Given the order the user created
    Given the user orders "Boeuf Bourguignon" and "Pavé de saumon" from the given restaurant
    When the user deletes "Pavé de saumon"
    Then "Pavé de saumon" doesn't appear in the cart anymore

  Scenario: Pay the order
    Given the order the user created
    Given the user orders "Boeuf Bourguignon" and "Pavé de saumon" from the given restaurant
    When the user wants to pay for the items in its cart
    Then the user pays the order and the order is closed
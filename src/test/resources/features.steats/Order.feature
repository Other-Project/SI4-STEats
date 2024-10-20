Feature: Order

  Background:
    Given an user of id "140403"
    Given a restaurant named "La Cafet"

  Scenario: Creating an order
    When the user creates an order and specifies a date, an address and a restaurant
    Then the user can order

  Scenario: Adding items to the cart
    Given an order to be delivered at "123456"
    When the user orders the following items from the given restaurant:
      | menuItems         |
      | Boeuf Bourguignon |
      | Pavé de saumon    |
    Then the items are added to his cart

  Scenario: Remove item from the cart
    Given an order to be delivered at "123456"
    Given the user orders the following items from the given restaurant:
      | menuItems         |
      | Boeuf Bourguignon |
      | Pavé de saumon    |
    When the user deletes "Pavé de saumon"
    Then "Pavé de saumon" doesn't appear in the cart anymore

  Scenario: Pay the order
    Given an order to be delivered at "123456"
    Given the user orders the following items from the given restaurant:
      | menuItems         |
      | Boeuf Bourguignon |
      | Pavé de saumon    |
    When the user pays for the items in its cart
    Then the user pays the order and the order is closed
Feature: Order

  Background:
    Given an user of name "Alban" and with userId "140403"

  Scenario: Creating an order
    When "Alban" creates an order and specifies a date
    Then A new order is created

  Scenario: Can only order available menu items






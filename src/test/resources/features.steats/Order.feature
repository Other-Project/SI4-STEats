Feature: Order

  Background:
    Given an user of name "Alban" and with userId "140403"
    Given a restaurant named "La Cafet"

  Scenario: Creating an order
    When "Alban" creates an order and specifies a date, an address and a restaurant
    Then "Alban" can order






Feature: Order

  Background:
    Given an user of id "140403"
    Given a restaurant named "La Cafet"

  Scenario: Creating an order
    When the user creates an order and specifies a date, an address and a restaurant
    Then the user can order

  Scenario: Filtering restaurants by name
    Given a restaurant named "mcdonalds"
    Given a restaurant named "Mcdonalds"
    When The user filter by typing "mc"
    Then The list of all restaurant containing "mc" are displayed


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
    Given a restaurant named "Le Manont"
    When The user filter by typing "mc"
    Then The list of all restaurant containing "mc" are displayed

  Scenario: Filtering restaurants by type of food
    Given a restaurant named "Burger King" of type "FAST_FOOD"
    Given a restaurant named "McDonald" of type "FAST_FOOD"
    Given a restaurant named "La chèvre d'or" of type "CLASSIC"
    When The user select "FAST_FOOD" and thus filter by type of food
    Then The list of all restaurant of type "FAST_FOOD" are displayed

  Scenario: Filtering restaurants that can deliver during a certain time
    Given an available restaurant named "Macdonald available 1" with schedule that starts at "10:00:00"
    Given an available restaurant named "Macdonald available 2" with schedule that starts at "9:30:00"
    Given an available restaurant named "Macdonald available 3" with schedule that starts at "9:00:00"
    Given an available restaurant named "Macdonald available 4" with schedule that starts at "8:30:00"
    Given a fully booked restaurant named "Macdonald full booked" with schedule that starts at "10:00:00"
    When The user choose to filter all restaurant that can deliver a MenuItem for "2024-03-29T10:15:30"
    Then The list of all restaurant that can deliver at least one MenuItem for "2024-03-29T10:15:30" are displayed

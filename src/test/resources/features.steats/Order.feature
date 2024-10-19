Feature: Order

  Background:
    Given an user of id "140403"
    Given a restaurant named "La Cafet"

  Scenario: Creating an order
    When the user creates an order and specifies a date, an address and a restaurant
    Then the user can order

  Scenario: Filtering restaurants by name
    When The user filter by typing "mc" and we have the following restaurants in the database:
      | name      |
      | mcdonalds |
      | Mcdonalds |
      | Le Manont |
      | Mc burger |
      | Le Mac    |
    Then The list of all restaurant displayed should contain the following restaurants:
      | name      |
      | mcdonalds |
      | Mcdonalds |
      | Mc burger |

  Scenario: Filtering restaurants by type of food
    When The user select "FAST_FOOD" and we have the following restaurants in the database:
      | name           | typeOfFood |
      | Burger King    | FAST_FOOD  |
      | McDonald       | FAST_FOOD  |
      | La chevre d'or | CLASSIC    |
    Then The list of all restaurant displayed should contain the following restaurants:
      | name        |
      | Burger King |
      | McDonald    |

  Scenario: Filtering restaurants that can deliver during a certain time
    When The user choose to filter all restaurant that can deliver a MenuItem for "2024-03-29T10:15:30" and we have the following restaurants in the database:
      | name                  | scheduleStart | orderDuration |
      | Macdonald available 0 | 10:30:00      | 15            |
      | Macdonald available 1 | 10:00:00      | 15            |
      | Macdonald available 2 | 9:30:00       | 15            |
      | Macdonald available 3 | 9:00:00       | 15            |
      | Macdonald available 4 | 8:30:00       | 15            |
      | Macdonald full booked | 10:00:00      | 30            |
    Then The list of all restaurant displayed should contain the following restaurants:
      | name                  |
      | Macdonald available 2 |
      | Macdonald available 3 |
      | Macdonald available 4 |

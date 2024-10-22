Feature: Order

  Background:
    Given an user of id "140403"
    Given a restaurant named "La Cafet"

  Scenario: Creating an order
    When the user creates an order and specifies a date, an address and a restaurant :
      | date     | addressId | restaurant |
      | 10:15:30 | Sophia    | La Cafet   |
    Then the user can order

  Scenario: Filtering restaurants by name
    Given The following restaurants :
      | name      |
      | mcdonalds |
      | Mcdonalds |
      | Le Manont |
      | Mc burger |
      | Le Mac    |
    When The user filter by typing "mc"
    Then The list of all restaurant displayed should contain the following restaurants:
      | name      |
      | mcdonalds |
      | Mcdonalds |
      | Mc burger |

  Scenario: Filtering restaurants by type of food
    Given The following restaurants with type of food :
      | name           | typeOfFood |
      | Burger King    | FAST_FOOD  |
      | McDonald       | FAST_FOOD  |
      | La chevre d'or | CLASSIC    |
    When The user filter by selecting "FAST_FOOD"
    Then The list of all restaurant displayed should contain the following restaurants:
      | name        |
      | Burger King |
      | McDonald    |

  Scenario: Filtering restaurants that can deliver during a certain time
    Given The address labelled "Campus Sophia Tech"
    Given The following restaurants with schedule and order duration and order scheduled to "2024-03-29T10:15:30" :
      | name                  | scheduleStart | preparationTime |
      | Macdonald available 0 | 10:30:00      | 15              |
      | Macdonald available 1 | 10:00:00      | 15              |
      | Macdonald available 2 | 9:30:00       | 15              |
      | Macdonald available 3 | 9:00:00       | 15              |
      | Macdonald available 4 | 8:30:00       | 15              |
      | Macdonald full booked | 10:00:00      | 30              |
    When The user filter by selecting a delivery time of "2024-03-29T10:15:30"
    Then The list of all restaurant displayed should contain the following restaurants:
      | name                  |
      | Macdonald available 2 |
      | Macdonald available 3 |
      | Macdonald available 4 |

  Scenario: Filtering restaurants by name and type of food
    Given The following restaurants with type of food :
      | name           | typeOfFood |
      | mcdonalds      | FAST_FOOD  |
      | Mcdonalds      | FAST_FOOD  |
      | Le Manont      | CLASSIC    |
      | Mc gastronomic | GOURMET    |
      | Burger King    | FAST_FOOD  |
    When The user filter by typing "mc" and selecting "FAST_FOOD"
    Then The list of all restaurant displayed should contain the following restaurants:
      | name      |
      | mcdonalds |
      | Mcdonalds |

  Scenario: Adding items to the cart
    Given an order to be delivered at "123456"
    When the user orders the following items from the given restaurant:
      | menuItems         |
      | Boeuf Bourguignon |
      | Pavé de saumon    |
    Then the following items are in his cart:
      | menuItems         |
      | Boeuf Bourguignon |
      | Pavé de saumon    |

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
    Then the order has the "PAID" status
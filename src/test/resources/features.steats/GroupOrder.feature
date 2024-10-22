#language: en
Feature: Manage GroupOrder

  Background:
    Given The user named "Alex" with the id "1234" is logged in
    Given The user named "Alban" with the id "4321" is logged in
    Given The user named "Alexandre" with the id "2323" is logged in
    Given The user named "Alexandra" with the id "3232" is logged in
    Given A restaurant named "McDonald's" with the following schedules :
      | start | duration | capacity |
      | 08:20 | 30       | 5        |
      | 08:50 | 30       | 5        |
      | 09:20 | 30       | 5        |
      | 09:50 | 30       | 5        |
      | 10:20 | 30       | 5        |
    Given The restaurant named "McDonald's" have the following menu :
      | name         | price | preparationTime |
      | Alban_Burger | 30.0  | 29              |
      | bigmac       | 10.0  | 10              |
      | bigmac++     | 20.0  | 20              |
      | coke         | 2.0   | 01              |
      | fries        | 3.0   | 05              |
      | icecream     | 4.0   | 02              |
      | salad        | 5.0   | 03              |
    Given A group order with the group code "groupCode2" from the restaurant "McDonald's" and to deliver for tomorrow at "10:15:30" at "292 Chemin de la Rigolade"

  Scenario: Create GroupOrder with a delivery time
    When "Alexandre" creates a group order from the restaurant "McDonald's" and to deliver for tomorrow at "10:15:30" at "292 Chemin de la Rigolade"
    Then "Alexandre" receives a group code
    And "Alexandre" can't change the delivery time to tomorrow at "18:15:30" to the group order

  Scenario: Create GroupOrder without a delivery time
    When "Alexandra" creates a group order from the restaurant "McDonald's" and to deliver at "292 Chemin de la Rigolade"
    Then "Alexandra" receives a group code
    And "Alexandra" can add tomorrow at "18:15:30" as delivery time to the group order

  Scenario: Join GroupOrder
    When "Alex" joins the group order with the group code "groupCode2"
    Then "Alex" is added to the group order with the group code "groupCode2"

  Scenario: Join GroupOrder with a wrong group code
    Then "Alex" can't join the group order with the group code "groupCode3"

  Scenario: Multiple GroupOrder
    Given A group order with the group code "groupCode89" from the restaurant "McDonald's" and to deliver for tomorrow at "10:15:30" at "292 Chemin de la Rigolade"
    When "Alex" joins the group order with the group code "groupCode2"
    When "Alban" joins the group order with the group code "groupCode89"
    Then "Alex" is added to the group order with the group code "groupCode2"
    And "Alban" is added to the group order with the group code "groupCode89"

  Scenario: Add Item to GroupOrder
    When "Alex" joins the group order with the group code "groupCode2"
    When "Alex" adds the item named "bigmac" with a price of 10.0 to the group order
    Then The item with named "bigmac" with a price of 10.0 is added to the order of "Alex" in the group order with the group code "groupCode2"

  Scenario: Close Group Order
    Given "Alex" joins the group order with the group code "groupCode2"
    Given "Alban" joins the group order with the group code "groupCode2"
    Given "Alex" adds the item named "bigmac" with a price of 10.0 to the group order
    Given "Alban" adds the item named "bigmac++" with a price of 20.0 to the group order
    When "Alex" pays
    Then The order of "Alex" is payed
    And "Alex" can't close the group order

  Scenario: Can close Group Order completely
    Given "Alex" joins the group order with the group code "groupCode2"
    Given "Alban" joins the group order with the group code "groupCode2"
    Given "Alex" adds the item named "bigmac" with a price of 10.0 to the group order
    Given "Alban" adds the item named "bigmac++" with a price of 20.0 to the group order
    Given "Alex" pays
    When "Alban" pays
    Then The order of "Alban" is payed
    And "Alban" can close the group order

  Scenario: Close Group Order completely
    Given "Alex" joins the group order with the group code "groupCode2"
    Given "Alban" joins the group order with the group code "groupCode2"
    Given "Alex" adds the item named "bigmac" with a price of 10.0 to the group order
    Given "Alban" adds the item named "bigmac++" with a price of 20.0 to the group order
    Given "Alex" pays
    Given "Alban" pays
    When "Alex" close the group order
    Then the group order with the id "groupCode2" is closed
    And The order containing the item "bigmac" is added to the history of "Alex"
    And The order containing the item "bigmac++" is added to the history of "Alban"
    And "Alex" can't close the group order
    And "Alban" can't close the group order

  Scenario: Close Group order if delivery time not set
    Given A restaurant named "BurgerKing" with the following schedules :
      | start | duration | capacity | day    |
      | 11:20 | 30       | 2        | MONDAY |
      | 11:50 | 30       | 1        | MONDAY |
      | 12:20 | 30       | 1        | MONDAY |
      | 12:50 | 30       | 1        | MONDAY |
      | 13:20 | 30       | 3        | MONDAY |
      | 14:50 | 30       | 1        | MONDAY |
      | 15:20 | 30       | 3        | MONDAY |
    Given The restaurant named "BurgerKing" have the following menu :
      | name         | price | preparationTime |
      | Alban_Burger | 30.0  | 29              |
      | bigmac       | 10.0  | 10              |
      | bigmac++     | 20.0  | 20              |
      | coke         | 2.0   | 01              |
      | fries        | 3.0   | 05              |
      | icecream     | 4.0   | 02              |
      | salad        | 5.0   | 03              |
    Given A group order with the group code "groupCode6" from the restaurant "BurgerKing" at "292 Chemin de la Rigolade"
    Given "Alex" joins the group order with the group code "groupCode6"
    Given "Alban" joins the group order with the group code "groupCode6"
    Given "Alexandra" joins the group order with the group code "groupCode6"
    Given "Alex" adds the item named "bigmac" with a price of 10.0 and a preparation time of 00:10 to the group order
    Given "Alban" adds the item named "Alban_Burger" with a price of 30.0 and a preparation time of 00:40 to the group order
    Given "Alban" adds the item named "bigmac++" with a price of 20.0 and a preparation time of 0:20 to the group order
    Given "Alban" adds the item named "fries" with a price of 3.0 and a preparation time of 00:05 to the group order
    Given "Alexandra" adds the item named "icecream" with a price of 4.0 and a preparation time of 00:02 to the group order
    Given "Alex" pays
    Given "Alban" pays
    Given "Alexandra" pays
    When "Alex" close the group order that doesn't have a delivery time
    Then "Alex" he need to choose the delivery time so he gets the next 2 delivery time from tomorrow at "12:30:00" and gets :
      | deliveryTime |
      | 14:00        |
      | 14:30        |
    And "Alex" can choose the following delivery time : "14:00"
          # also 14:30 or 15:00 are possible but he can only change the delivery time once
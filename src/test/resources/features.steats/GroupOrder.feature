#language: en
Feature: Manage GroupOrder

  Background:
    Given The user named "Alex" with the id "1234" is logged in
    Given The user named "Alban" with the id "4321" is logged in
    Given The user named "Alexandre" with the id "2323" is logged in
    Given The user named "Alexandra" with the id "3232" is logged in
    Given A group order with the group code "groupCode2" from the restaurant "McDonald's" and to deliver for "2024-03-29T10:15:30" at "292 Chemin de la Rigolade"

  Scenario: Create GroupOrder with a delivery time
    When "Alexandre" creates a group order from the restaurant "McDonald's" and to deliver for "2024-03-29T10:15:30" at "292 Chemin de la Rigolade"
    Then "Alexandre" receives a group code
    And "Alexandre" can't change the delivery time to "2024-03-29T10:18:30" to the group order

  Scenario: Create GroupOrder without a delivery time
    When "Alexandra" creates a group order from the restaurant "McDonald's" and to deliver at "292 Chemin de la Rigolade"
    Then "Alexandra" receives a group code
    And "Alexandra" can add "2024-03-29T10:15:30" as delivery time to the group order

  Scenario: Join GroupOrder
    When "Alex" joins the group order with the group code "groupCode2"
    Then "Alex" is added to the group order with the group code "groupCode2"

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
    And The order is added to the history of "Alex"
    And The order is added to the history of "Alban"
    And "Alex" can't close the group order
    And "Alban" can't close the group order

#language: en
Feature: Manage GroupOrder

  Background:
    Given The user named "Alex" with the id "1234" is logged in
    Given The user named "Alban" with the id "4321" is logged in
    Given A group order with the group code "groupCode2" from the restaurant "McDonald's" and to deliver for "2024-03-29T10:15:30" at "292 Chemin de la Rigolade"

  Scenario: Join GroupOrder
    When The user with the id "1234" joins the group order with the group code "groupCode2"
    Then The user with the id "1234" is added to the group order with the group code "groupCode2"

  Scenario: Add Item to GroupOrder
    When The user with the id "1234" joins the group order with the group code "groupCode2"
    When The user with the id "1234" adds the item named "bigmac" with a price of 10.0 to the group order
    Then The item with named "bigmac" with a price of 10.0 is added to the order of the user with the id "1234" in the group order with the group code "groupCode2"

  Scenario: Close Group Order
    Given The user with the id "1234" joins the group order with the group code "groupCode2"
    Given The user with the id "4321" joins the group order with the group code "groupCode2"
    Given The user with the id "1234" adds the item named "bigmac" with a price of 10.0 to the group order
    Given The user with the id "4321" adds the item named "bigmac++" with a price of 20.0 to the group order
    When The user with the id "1234" pay
    Then The order of the user with the id "1234" is payed
    And The user with the id "1234" can't close the group order

  Scenario: Can close Group Order completely
    Given The user with the id "1234" joins the group order with the group code "groupCode2"
    Given The user with the id "4321" joins the group order with the group code "groupCode2"
    Given The user with the id "1234" adds the item named "bigmac" with a price of 10.0 to the group order
    Given The user with the id "4321" adds the item named "bigmac++" with a price of 20.0 to the group order
    Given The user with the id "1234" pay
    When The user with the id "4321" pay
    Then The order of the user with the id "4321" is payed
    And The user with the id "4321" can close the group order

  Scenario: Close Group Order completely
    Given The user with the id "1234" joins the group order with the group code "groupCode2"
    Given The user with the id "4321" joins the group order with the group code "groupCode2"
    Given The user with the id "1234" adds the item named "bigmac" with a price of 10.0 to the group order
    Given The user with the id "4321" adds the item named "bigmac++" with a price of 20.0 to the group order
    Given The user with the id "1234" pay
    Given The user with the id "4321" pay
    When The user with the id "1234" close the group order
    Then the group order with the id "groupCode2" is closed
    And The order is added to the history of the user with the id "1234"
    And The order is added to the history of the user with the id "4321"
    And The user with the id "1234" can't close the group order
    And The user with the id "4321" can't close the group order








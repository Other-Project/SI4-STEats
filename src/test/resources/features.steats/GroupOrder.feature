#language: en
Feature: Manage GroupOrder

  Background:
    Given The user named "Alex" with the id "1234" is logged in

  Scenario: Join GroupOrder
    Given A group order with the group code "groupCode1" from the restaurant "McDonald's" and to deliver for "2024-03-27T10:15:30" at "292 Chemin de la Rigolade"
    When The user joins the group order with the group code "groupCode1"
    Then The user with the id "1234" is added to the group order with the group code "groupCode1"

  Scenario: Add Item to GroupOrder
    Given A group order with the group code "groupCode2" from the restaurant "McDonald's" and to deliver for "2024-03-29T10:15:30" at "292 Chemin de la Rigolade"
    When The user joins the group order with the group code "groupCode2"
    When The user adds the item named "bigmac" with a price of 10.0 to the group order
    Then The item with named "bigmac" with a price of 10.0 is added to the order of the user with the id "1234" in the group order with the group code "groupCode2"


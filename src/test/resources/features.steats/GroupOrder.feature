#language: en
Feature: Manage GroupOrder

  Background:
    Given The user with the id "01234" is logged in

  Scenario: Join GroupOrder
    Given A group order is created with the group code "groupCode1" and the delivery time "2019-03-27T10:15:30" and the address "292 Chemin de la Rigolade" and the restaurant "McDonald's"
    When The user with the id "01234" joins the group order with the group code "groupCode1"
    Then The user with the id "01234" is added to the group order with the group code "groupCode1"

  Scenario: Add Item to GroupOrder
    Given A group order is created with the group code "groupCode2" and the delivery time "2019-03-27T10:15:30" and the address "292 Chemin de la Rigolade" and the restaurant "McDonald's"
    When The user with the id "01234" joins the group order with the group code "groupCode2"
    When The user with the id "01234" adds the item named "BigMac" with a price of 10.0 to the group order with the group code "groupCode2"
    Then The item with named "BigMac" is added to the order of the user with the id "01234" in the group order with the group code "groupCode2"




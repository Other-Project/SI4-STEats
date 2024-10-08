#language: en
Feature: Manage GroupOrder

  Background:
    Given A group order is created with the group code "groupCode" and the delivery time "2024-010-28T10:15:30" and the address "292 Chemin de la Rigolade" and the restaurant "McDonald's"

  Scenario: Join GroupOrder
    When The user with the id "69420" joins the group order with the group code "groupCode"
    Then The user with the id "69420" is added to the group order with the group code "groupCode"




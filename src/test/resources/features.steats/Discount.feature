#language: en
Feature: Manage discounts

  Background:
    Given a restaurant named "R1" of type "CLASSIC"
    And a discount of 5% each 10 orders
    And a discount of 10% if the client has the "STUDENT" role

    Given a restaurant named "R2" of type "CLASSIC"
    And each 10 orders, an offer of the following free products:
      | name |
      | P2   |

  Scenario: Free product
    Given I am a client with the "FACULTY" role and 9 orders at "R2"
    When I place an order at "R2" with the following items:
      | name |
      | P1   |
    Then My cart should contain the following items:
      | name |
      | P1   |
      | P2   |

  Scenario: Discount for 10 orders
    Given I am a client with the "EXTERNAL" role and 9 orders at "R1"
    When I place an order at "R1" with the following items:
      | name |
      | P1   |
    Then I should receive a 5% discount



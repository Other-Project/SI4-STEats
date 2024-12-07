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
    And a discount of 0.50€ if the order has more than 3 items
    And a discount of 1€ the next time if the order has more than 4 items

  Scenario: Free product
    Given I am "Robert" with the "FACULTY" role and 9 orders at "R2" of 2 items
    When I place an order at "R2" with the following items:
      | name |
      | P1   |
    Then My cart should contain the following items:
      | name |
      | P1   |
      | P2   |

  Scenario: Discount for 10 orders
    Given I am "Bob" with the "EXTERNAL" role and 9 orders at "R1" of 2 items
    When I place an order at "R1" with the following items:
      | name |
      | P1   |
    Then I should receive a 5% discount

  Scenario: No discount
    Given I am "Bob" with the "EXTERNAL" role and 8 orders at "R1" of 2 items
    When I place an order at "R1" with the following items:
      | name |
      | P1   |
    Then I shouldn't receive a discount

  Scenario: Don't repeat discount
    Given I am "Bob" with the "EXTERNAL" role and 10 orders at "R1" of 2 items
    When I place an order at "R1" with the following items:
      | name |
      | P1   |
    Then I shouldn't receive a discount

  Scenario: But sometimes repeat discount
    Given I am "Bob" with the "EXTERNAL" role and 19 orders at "R1" of 2 items
    When I place an order at "R1" with the following items:
      | name |
      | P1   |
    Then I should receive a 5% discount

  Scenario: Stacked discounts
    Given I am "Andy" with the "STUDENT" role and 9 orders at "R1" of 2 items
    When I place an order at "R1" with the following items:
      | name |
      | P1   |
    Then I should receive a 14.5% discount

  Scenario: Cash discount
    Given I am "Robert" with the "FACULTY" role and 1 orders at "R2" of 2 items
    When I place an order at "R2" with the following items:
      | name |
      | P1   |
      | P1   |
      | P2   |
    Then I should receive a 0.5€ discount

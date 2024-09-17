# Glossary

## Actors

User : Person that uses our system  
Guest : Unregistered User  
Client : Registered User that is not working as restaurant Staff nor delivery person  
Restaurant Staff : A person employed by the restaurant to prepare meal  
Restaurant Manager : A restaurant staff that can update menus offering and opening hours  
Delivery Person : Person in charge of the delivery of the meal  
Administrators : Person that have control over restaurant partnerships and delivery services.

# Use-Case Diagram

```plantuml
@startuml
left to right direction
actor "Restaurant Manager" as manager
actor "Restaurant Staff" as restaurant
actor "Client" as client
actor "Delivery Person" as delivery
actor "    {Abstract}\nRegistered User" as registered_user
actor "Guest" as guest
actor "{Abstract}\n     User" as user
rectangle {
  usecase "Browse restaurants" as UC1
  usecase "Browse menu" as UC2
  usecase "Update opening hours" as UC10
  usecase "Update menus offerings" as UC20
  usecase "Set preparation times" as UC30
  usecase "Manage orders" as UC40
  usecase "Place order" as UC3
  usecase "Create group order" as UC4
  usecase "Validate group order" as UC5
  usecase "Browse historic" as UC6
  usecase "Payment step" as UC7
}
actor "Payment system" as payment_system
guest ---|> user
client ---|> registered_user
manager ---|> restaurant
delivery --|> registered_user
restaurant ---|> registered_user
registered_user ---|> user

user ---> UC1
UC1 <. UC2 : extends
 
UC3 --> UC1 : includes
registered_user ---> UC4 
registered_user ---> UC6
UC4 --> UC3 : includes
UC4 --> UC7
UC7 <. UC5 : extends
UC7 <--- payment_system

restaurant --> UC40
manager ---> UC10
manager ---> UC20
UC20 <. UC30 : extends

@enduml
```
![Plant UML preview](https://ptuml.hackmd.io/svg/TPJTQkCm48NlzHH3xhEqITf0AQ6xAVHgeRHahmf5jSUEm9RCIDvjQFlkgrzR3cx0yVZCfwEnPl2TDfnCtpQimSg0KK2YFXee1M5XX9AC5qOHB9xGaZrnQU0tbxn6MW3Nq8PuXdeslAeyGuEOi7qZK1gVB7oO4lVOYBz89tX4qafweenQOi3-Fdw-QKDM-7gHJrWBRP2mX8EEPT6WlVPwBE-XjwLue7PHadCtvpxw-CNk9BYi6uGF1augk4PO_2Brpzx7jzD-oM5_EKErAFkONkNfGrToWw0wb4BMS5Gz3JONCw3pqQ2g2ido2LrDq6Sqq15sdBZh81ZHOYBN4p9q4XILE6oxcH2F3IyYa1poz9xGLLMJwhi9jScfFxmHvJntDNDMHziwHQA8oFMa7dwoHs30DhUB-ItxOhwTi5mkFtUXPs6ied8-3N5eX-mmdcdKO7RP27xZU_QSryEIiFC3OY-u_U4w1pU0xmPbgHbOUGqHiRgGHTENgETy7RE1xpBNp6L3lCwTHdbhusseuIghmOlM1RfmqA_wPFrRDdtvNPYJx1ntOM3F53kNxYziixx8zc5tzkO-E_y1)

# Class Diagram

```mermaid
classDiagram

class Role {
    <<Enumeration>>
    CAMPUS_USER
    RESTAURANT_STAFF
    RESTAURANT_MANAGER
    DELIVERY_PERSON
    CAMPUS_ADMIN
}

class User {
    last_name : String
    first_name : String
    email : String
    password : String
}

class Restaurant {
    name : String
}
class Schedule {
    start : Time
    end : Time
    nb_person : int
}

class MenuItem {
    name : String
    price : double
    preparation : Time
}

class Order {
    delivery_time: DateTime
    price : double
    discount : int
}

class GroupOrder {
    id: String
    delivery_time : DateTime
}

class Address {
    street : String
    city : String
    postal_code : String
    additional_adress : String
}

class Status {
    <<Enumeration>>
    INITIALISED
    COMPLETING
    FINALISING
    IN_PREPARATION
    READY_FOR_DELIVERY
    IN_DELIVERY
    DELIVERED
}

Restaurant "1 restaurant    " -- " * schedules " Schedule
Restaurant "   1 restaurant" -- "1..* menu" MenuItem
User " * " --> "role 1 " Role
User "1 user " -- " * orders" Order
Order " * " --> "1 status" Status
Order " * orders" -- " 1..* items" MenuItem
Order " * orders" -- "1 address" Address
Order " * " -- " * " GroupOrder 
```

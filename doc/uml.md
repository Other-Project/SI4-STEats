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

## Browse restaurants or menus

```plantuml
@startuml
left to right direction
actor "{Abstract}\n     User" as user
actor "Guest" as guest
actor "Client" as client
actor "Restaurant manager" as manager
actor "Restaurant Staff" as restaurant
actor "Delivery Person" as delivery
rectangle {
  usecase "Browse restaurants" as UC1
  usecase "Browse menu" as UC2
}
guest --|> user
client --|> user
restaurant --|> user
delivery --|> user
manager --|> user
user --> UC1
UC1 <. UC2 : extends
@enduml
```

## Order and track 

```plantuml
@startuml
left to right direction
actor "    {Abstract}\nRegistered User" as user
actor "Client" as client
actor "Restaurant Staff" as restaurant
actor "Delivery Person" as delivery
actor "Restaurant Manager" as manager
rectangle {
  usecase "Browse restaurant" as UC1
  usecase "Payment step" as UC2
  usecase "Place order" as UC3
  usecase "Create group order" as UC4
  usecase "Validate group order" as UC5
  usecase "Browse historic" as UC6
}
actor "Payment system" as payment_system
manager --|> user
client --|> user
restaurant --|> user
delivery --|> user
UC3 --> UC1 : includes
user --> UC4 
user --> UC6
UC4 --> UC3 : includes
UC4 --> UC2
UC2 <. UC5 : extends
UC2 <--- payment_system
@enduml
```


## Manage store

```plantuml
@startuml
left to right direction
actor "Restaurant Managers" as user
actor "Restaurant Staff" as staff
rectangle {
    usecase "Update opening hours" as UC1
    usecase "Update menus offerings" as UC2
    usecase "Set preparation times" as UC3
    usecase "Manage orders" as UC4
}
user --> UC1
user --> UC2
staff --> UC4
user --|> staff
UC2 <. UC3 : extends
@enduml
```

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

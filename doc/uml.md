# Glossary

## Actors

**User :** Person that uses our system  
**Guest :** Unregistered User  
**Client :** Registered User that is not working as restaurant Staff nor delivery person  
**Restaurant Staff :** A person employed by the restaurant to prepare meal  
**Restaurant Manager :** A restaurant staff that can update menus offering and opening hours  
**Delivery Person :** Person in charge of the delivery of the meal  
**Administrators :** Person that have control over restaurant partnerships and delivery services.

# Use-Case Diagram

![Plant UML preview](https://www.plantuml.com/plantuml/svg/TPFHQzim4CRVzLSSUU_8bhI5CKgt3FOq22tPqw1ezjaHs98vdRQ5Bl_x94kAvU0AdCZVzzEdYtGHZvObiUixqM73m0P8jKU6MX5Mh8mMic93i7f2JpgIck6xrB95Me6qqCVv0lNCicb6XYvYndQTGisZMVdfP7p5Jlr6Ei4UoHez4dNICWD-l7x-PPcySFwfdx1Lbf6mXeDDPT55Ut5sAk-RGnktRSCiosKQ-37bb8ltyjIZPh0ddzoFzX2bboY9S6HIjnt2gu2mjf8MOVM5p1-BnJ7OSV5Xztw1ydaksHzA-p3KaX7CW5hf5ex6qILclG2670kcQP0yRtFa37r6Xe5ma2J3LGEh7ZEvcP7noi5GZPI9xOpOTx9AG4uexHrXgAebuuOPjIsf7x9JzJ9tkt1MHsNPaAeISbSWqsqn3ic_5sSntZlStFnxY7SRsoyArqsJUZkQHHFdXbnUDO7B_jLxKKjw3UDXx0x_mATtuThW8-1VHbrR0LxUG4AyhdJLkHhjGl38RE4jvqu4DyutPT8atml_n1fkfngYw5Ek_m3bZq-xh_Cnvf9dGcp9y14tsQoBNSIZ_t9zzny0)

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
actor "Payment system" as payment_system
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
guest --|> user
client --|> registered_user
manager --|> restaurant
delivery --|> registered_user
restaurant --|> registered_user
registered_user --|> user
user ------> UC1
UC1 <. UC2 : extends
 
UC3 --> UC1 : includes
registered_user ---> UC4 
registered_user ---> UC6
UC4 --> UC3 : includes
UC4 --> UC7
UC7 <. UC5 : extends
UC7 <--- payment_system

restaurant --> UC40
manager --> UC10
manager --> UC20
UC20 <. UC30 : extends

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

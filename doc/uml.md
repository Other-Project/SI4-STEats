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

![Plant UML preview](https://www.plantuml.com/plantuml/svg/TPFVQuCm4CVVyrSSVU_eQd_065BMXpqDoahtMeAUDg29N29RsVQ_xsAqQd45zVpU9zzSB-VEEackBWjHOER06I2LNnoaYZ1nocWX4sS8Pk_8P4rIEtYJMkP8Cv0MoX1FK4SdiwnXw2RsMAwiGy8KJhPrefjshctl-5hpwWR8VTJ91tajaNUnLrvJDlagIESW2L-_r7c1y2q0s3AH5c7sGkQJdtrDjbbysZzEKEnNj-dbC7sgKkaGJ8LQwHmkfgQRpM82z3uMJ9OXCT-Xon5wH0SLOIL9-hw3KoLsP3GYG__1K8gtRLSZub38f0KwXs5wJ-YhoidKrOXQ3QaFMQXqcbjFzEh2fsT89IsocRHgpwXbjk9Nj0C4y_bFF9oKFknIu_6uUmzG697cpeB_nSt9yWNF3xxby0JuvL2dLW3B4RG8wqedHPsYdR1kc1Nybza8dmrnD7JgDzXol0qrh0SrD2Axt0_l-E_7uO3xLiLXqCO2ZvE_XKsYnM0NiUE7_yR_0000)

```plantuml
@startuml
left to right direction
actor "Restaurant Manager" as manager
actor "Restaurant Staff" as restaurant
actor "Registered User" as registered_user
actor "Guest" as guest
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
  usecase "Validate payment" as UC7
}
manager --|> restaurant
restaurant --|> registered_user
registered_user --|> guest
guest ------> UC1
UC1 <. UC2 : extends
 
UC3 --> UC1 : includes
registered_user ---> UC4 
registered_user ---> UC6
UC4 --> UC3 : includes
UC3 --> UC7
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
    preparation_time : Time
}

class TypeOfFood {
    <<Enumeration>>
    FAST_FOOD
    DRINKS
    SNACKS
    DESERTS
}

class Order {
    delivery_time : DateTime
    user_id : String
}

class GroupOrder {
    group_code : String
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


Order "* orders " -- "1 group " GroupOrder
Restaurant "1 restaurant" -- "1..* menu" MenuItem
MenuItem "1..* items"  --  "* orders" Order
Restaurant "1 restaurant" -- "* orders" Order
Status "1 status" <-- "* " Order
Restaurant "1 restaurant" -- " * schedules" Schedule
Address "1 address" -- "* orders" Order
TypeOfFood "1 type_of_food" <-- "*" Restaurant 

```

# Sequence Diagram

## Group order
```mermaid
sequenceDiagram
    actor User
    activate STEats
    activate GroupOrder
    STEats->>GroupOrder : createOrder()
    create participant Order
    GroupOrder-->>Order: <<create>>
    activate Order
    GroupOrder-->>STEats: Order
    STEats->>GroupOrder: getMenu()
    GroupOrder->>Restaurant: getMenu(time)
    activate Restaurant
    activate Schedule
    loop
        Restaurant->>Schedule : getNbPerson()
        Schedule-->>Restaurant: menuItem
        Restaurant->>Schedule : getstart()
        Schedule-->>Restaurant: menuItem
        Restaurant->>Schedule : getend()
        Schedule-->>Restaurant: menuItem
    end
    loop
        Restaurant->>MenuItem : getPreparationTime()
        MenuItem -->> Restaurant : Time
    end
    Restaurant-->>GroupOrder: MenuItem[]
    deactivate Restaurant
    note right of GroupOrder : Returns only menu items that can be delivered in time
    GroupOrder-->>STEats: MenuItem[]
    loop
        User->>STEats:Choose menu item
        STEats->>Order:addMenuItem(menuItem)
    end
    User->>STEats:Proceed to payment
    STEats->>Order: getTotalPrice()
    Order-->>STEats: price
    deactivate Order
    STEats->>PaymentSystem: pay()
    PaymentSystem-->>STEats: PaymentStatus
    opt
        User->>STEats: Validate Group Order
        STEats->>GroupOrder: closeGroupOrder()
    end
    deactivate GroupOrder
    deactivate STEats
```

## Search for a meal

```mermaid
sequenceDiagram
    actor User
    activate STEats
    opt 
        User ->> STEats : Select type of food
    end
    opt 
        User ->> STEats : Enter a restaurant
    end
    User->> STEats : Choose a restaurant
    STEats->>Restaurant : getMenuItems()
    activate Restaurant
    Restaurant-->>STEats : MenuItem[]
    deactivate Restaurant
    deactivate STEats
```

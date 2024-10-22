# Group members

* **Falcoz** Alban (SA)
* **Galli** Evan (QA)
* **Gripari** Alexandre (Ops)
* **Lassauniere** Theo (PO)


# Hypothesis and limits found

* Orders within a group order can only be placed at the same restaurant
* We try to prepare the menu item during the schedule (30 min for example) before the delivery time. If the item can't be prepared because the schedule is too tight, we try to prepare it during the previous schedule, up until 2 hours before. If it can't be prepared during any of the schedules 2 hours before then the item is not available.

# Glossary

* **Guest :** A person browsing the platform without being authenticated
* **Registered User :** Any registered member of the campus (students, staff, ...)
* **Restaurant Staff :** A person employed by the restaurant to prepare meal
* **Restaurant Manager :** A restaurant staff that can update menus offering and opening hours

---

* **Menu:** All the menu items propose by a restaurant
* **MenuItem:** A item propose by a restaurant (ex : ice-cream, fries, burger, soda, ...) 

# Use-Case Diagram

![Plant UML preview](https://www.plantuml.com/plantuml/svg/XPD1Yzim48Nl_XL3xYvfassWb6MR77egB5tIwp9hZns1BPdHgAtfzhzNigp6AgO9H4XlldaQnFE35MHnVLTqr0Y80LRjHQ1MJ9Keemkin31ilf8d7QCM-88QM-8De8K-xbUe9y6ccHZ-ArwnLbaXfXhEbdYcalRixDNni_FT4z26NP8VSUn92zZHz_HJVOZISvIAC3_gjYFuLG1uomejmUR8veUbnKnsQZwVtlwVWfSHDFQKw4-tSHa70jD0OqoTsewts_0xgWvVLATaJ6sdbRQUj9lhxvRbyr2Z49Y1jD8jN8nZctosAs3miM2QXjZpDbbcw1C934m3CePGWAYU4hdBo1WEC5mJ9sAV4OyTLZEG79Rb4rEOgcNZXepQBwblsAbwdRjRUQkBic9OLJFoViqg1MXc3iNlOautbENh_JB6rsqgvTdDpga_vdTQePn-zrFi_1y-lWbF3X-0VWhfsaRnjInZr9QdOtRomV3d7Sncta7fgdCrsPKX9cOFwvNmAg4Qzxkbq_M2GouVuk1tsU1Uz8R_VexPcyL1jkbzq_IP43CObdZ9RhkuvS6lhk_-0000)

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
  usecase "Browse restaurants by name" as UC1A
  usecase "Browse restaurants by type of food" as UC1B
  usecase "Browse restaurants by availability" as UC1C
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
UC1 <|-- UC1A
UC1 <|-- UC1B
UC1 <|-- UC1C
 
UC3 --> UC1 : includes
registered_user ---> UC4 
registered_user ---> UC6
UC4 --> UC3 : includes
UC3 --> UC7 : includes
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
direction BT
class AbstractManager~T~ {
  + add(String, T) void
  + contains(String) boolean
  + get(String) T
  + remove(String) void
  + clear() void
   List~T~ all
}
class Address {
  + label() String
  + postal_code() String
  + city() String
  + street() String
  + additional_address() String
}
class AddressManager {
   AddressManager INSTANCE
}
class Discount {
  + value(double) double
  + isApplicable(SingleOrder) boolean
  + freeItems() List~MenuItem~
  + getNewPrice(double) double
  + canBeAppliedDirectly() boolean
   boolean stackable
   boolean expired
}
class DiscountBuilder {
  + expiresAt(LocalDateTime) DiscountBuilder
  + neverExpires() DiscountBuilder
  + unstackable() DiscountBuilder
  + stackable() DiscountBuilder
  + build() Discount
  + appliesAfterOrder() DiscountBuilder
  + oneTimeOffer() DiscountBuilder
  + appliesDuringOrder() DiscountBuilder
   double orderCredit
   MenuItem[] freeItems
   Criteria criteria
   double orderDiscount
   int currentOrderItemsAmount
   Role[] userRoles
   Discounts discounts
   int ordersAmount
   Options options
   int itemsAmount
}
class GroupOrder {
  + pay(SingleOrder) boolean
  + closeOrder() void
  + createOrder(User) SingleOrder
  + getAvailableMenu(LocalDateTime) List~MenuItem~
   double price
   List~SingleOrder~ orders
   List~User~ users
   Restaurant restaurant
   Duration preparationTime
   String groupCode
   Address address
   LocalDateTime deliveryTime
   List~MenuItem~ items
   Status status
}
class GroupOrderManager {
   GroupOrderManager INSTANCE
}
class MenuItem {
  + hashCode() int
  + toString() String
  + equals(Object) boolean
   double price
   String name
   Duration preparationTime
}
class NotFoundException
class Order {
<<Interface>>
  + getAvailableMenu(LocalDateTime) List~MenuItem~
  + closeOrder() void
   List~User~ users
   Restaurant restaurant
   Duration preparationTime
   Address address
   LocalDateTime deliveryTime
   List~MenuItem~ items
   Status status
}
class PaymentSystem {
  + pay(double) boolean
}
class Restaurant {
  + addDiscount(Discount) void
  + equals(Object) boolean
  + getAvailableMenu(LocalDateTime) List~MenuItem~
  + removeMenuItem(MenuItem) void
  + hashCode() int
  + toString() String
  + removeDiscount(Discount) void
  - getMaxCapacityLeft(LocalDateTime) Duration
  + addSchedule(Schedule) void
  + addOrder(Order) void
  + availableDiscounts(SingleOrder) List~Discount~
  + addMenuItem(MenuItem) void
  + discounts() List~Discount~
  + canHandle(Order, LocalDateTime) boolean
  - capacityLeft(Schedule, LocalDateTime) Duration
   String name
   TypeOfFood typeOfFood
   List~Order~ orders
   List~MenuItem~ fullMenu
}
class RestaurantManager {
   RestaurantManager INSTANCE
}
class Role {
<<enumeration>>
  + values() Role[]
  + valueOf(String) Role
}
class STEats {
  + closeGroupOrder() void
  + canCloseGroupOrder() boolean
  + createOrder(LocalDateTime, String, Restaurant) void
  + payOrder() boolean
  + joinGroupOrder(String) void
  + addMenuItem(MenuItem) void
  + removeMenuItem(MenuItem) void
  - updateFullMenu(Order) void
  + createGroupOrder(String, LocalDateTime, String, Restaurant) void
   List~MenuItem~ availableMenu
   SingleOrder order
   List~MenuItem~ fullMenu
   double totalPrice
   List~MenuItem~ cart
   User user
   List~String~ addresses
}
class STEatsController {
  + logging(String) STEats
}
class Saleable {
<<Interface>>
   double price
}
class Schedule {
  + overlap(Schedule) boolean
  + isBetween(LocalDateTime, LocalDateTime) boolean
  + contains(LocalDateTime) Boolean
  + compareTo(Schedule) int
  + contains(Order) Boolean
  + toString() String
  + compareTo(LocalDateTime) int
   Duration totalCapacity
   Duration duration
   LocalTime end
   LocalTime start
}
class SingleOrder {
  + closeOrder() void
  + pay(boolean) boolean
  + addMenuItem(MenuItem) void
  + getAvailableMenu(LocalDateTime) List~MenuItem~
  + removeMenuItem(MenuItem) void
  + validateOrder() void
  - updateDiscounts() void
   List~Discount~ discounts
   Address address
   User user
   double price
   List~User~ users
   Restaurant restaurant
   Duration preparationTime
   List~Discount~ discountsToApplyNext
   double subPrice
   LocalDateTime deliveryTime
   List~MenuItem~ items
   Status status
   String userId
}
class SingleOrderManager {
   SingleOrderManager INSTANCE
}
class Status {
<<enumeration>>
  + valueOf(String) Status
  + values() Status[]
}
class TypeOfFood {
<<enumeration>>
  + values() TypeOfFood[]
  + valueOf(String) TypeOfFood
}
class User {
  + addOrderToHistory(SingleOrder) void
  + pay(double) boolean
  + getOrders(Restaurant) List~SingleOrder~
  + getDiscountsToApplyNext(Restaurant) List~Discount~
   String name
   List~Order~ orders
   Role role
   String userId
}
class UserManager {
  + fillForDemo() void
   UserManager INSTANCE
}

AbstractManager~T~  ..>  NotFoundException : «create»
AddressManager  -->  AbstractManager~T~ 
DiscountBuilder  ..>  Discount : «create»
GroupOrder  ..>  Order 
GroupOrder "1" *--> "restaurant 1" Restaurant 
GroupOrder  ..>  SingleOrder : «create»
GroupOrder "1" *--> "orders *" SingleOrder 
GroupOrder "1" *--> "status 1" Status 
GroupOrderManager  -->  AbstractManager~T~ 
MenuItem  ..>  Saleable 
Order  -->  Saleable 
Restaurant "1" *--> "discounts *" Discount 
Restaurant "1" *--> "menu *" MenuItem 
Restaurant "1" *--> "orders *" Order 
Restaurant "1" *--> "schedules *" Schedule 
Restaurant "1" *--> "typeOfFood 1" TypeOfFood 
RestaurantManager  -->  AbstractManager~T~ 
STEats  ..>  GroupOrder : «create»
STEats "1" *--> "fullMenu *" MenuItem 
STEats "1" *--> "order 1" SingleOrder 
STEats  ..>  SingleOrder : «create»
STEats "1" *--> "user 1" User 
STEatsController  ..>  NotFoundException : «create»
STEatsController  ..>  STEats : «create»
SingleOrder "1" *--> "appliedDiscounts *" Discount 
SingleOrder "1" *--> "items *" MenuItem 
SingleOrder  ..>  Order 
SingleOrder "1" *--> "restaurant 1" Restaurant 
SingleOrder "1" *--> "status 1" Status 
SingleOrderManager  -->  AbstractManager~T~ 
User "1" *--> "role 1" Role 
User "1" *--> "ordersHistory *" SingleOrder 
UserManager  -->  AbstractManager~T~ 
UserManager  ..>  User : «create»
```

# Sequence Diagram

## Individual order inside a group order

```mermaid
sequenceDiagram
    actor User
    activate STEats
    activate GroupOrder
    STEats->>GroupOrder : createOrder(userId)
    create participant Order
    GroupOrder-->>Order: <<create>>
    activate Order
    GroupOrder-->>STEats: Order
    STEats->>GroupOrder: getMenu()
    GroupOrder->>Restaurant: getMenu(time)
    activate Restaurant
    activate Schedule
    loop
        Restaurant->>MenuItem : getPreparationTime()
        MenuItem -->> Restaurant : Time
        loop
            Restaurant->>Schedule : canCook(time)
            Schedule-->>Restaurant: bool
        end
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
    opt
        User->>STEats: Use discount
        STEats->>Order: useDiscount()
    end
    STEats->>Order: getTotalPrice()
    Order-->>STEats: price
    STEats->>Order: pay()
    Order->>PaymentSystem: pay()
    PaymentSystem-->>Order: PaymentStatus
    Order->>Order:checkIfDiscount()
    alt hasDiscount
        Order->>Order: addDiscountToUser()
    end
    Order-->>STEats: PaymentStatus
    deactivate Order
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

# Mockup

![Mockup](https://github.com/user-attachments/assets/bcb43945-4bc5-4c89-8ac2-69bbf8f75c29)

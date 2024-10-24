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

![Plant UML preview](https://www.plantuml.com/plantuml/svg/XPDHQzim4CVV_IcEl6TaJTh06ALB7WQ3GLd9Nil5FZi2Mp8dQPjPzzqdMLOh3NKDH4Z_-ztfplxxJXjaOuU-wgarO1Imw2u66i5K6w5aXRLH3Ajlv4ZBA0ryHOaTyGfGmn3s1Uh1ODlE33-BBrWdj26c1awQUA6YzcYT5j7FrcNFGETtKRx7QI1tYvvSpZ37no0z1gdo_QFiUeBV5O0hMQCcM1rO_TIKzAJdvDFn_VyfE4yWSQ18VteRDzD8e5febMfYsk7jDFo1eiUpw8MPOjgnaEQ6jKlyAWsVnWODWHf92jd1HLb-dd5J07qT3QfjYHqVqQiCVI038zE8ZDuKOCH0aTncP307A6w88x7BYFiUwmM85TBmaSbtrR6oOqRjKkgB4hB4d5FeE_QYAHUxBZpGYz16iQWNvAPKAhfiOVRLdshv162zVhfDlVwoZQ7SuDavvWUJpwiJ_EzszgRxmyTt_ht01w1VXcIZW_YqNWS_fgT3TdBkSUSjBCLS1I7htZQa2qtCp0vUZPnVYznKFY_ij-aTFgcinvRskRmFervdephHNVJlLvuzvT3W9hwHE6yc16lx9LootIIttBdL3lrV)

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
  usecase "Join group order" as UC4b
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
registered_user ---> UC4b
registered_user ---> UC6
UC4 --> UC3 : includes
UC4b --> UC3 : includes
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
  + contains(String) boolean
  + add(String, T) void
  + get(String) T
  + remove(String) void
  + clear() void
   List~T~ all
}
class Address {
  + city() String
  + additional_address() String
  + postal_code() String
  + street() String
  + label() String
}
class AddressManager {
   AddressManager INSTANCE
}
class Discount {
  + isApplicable(SingleOrder) boolean
  + canBeAppliedDirectly() boolean
  + getNewPrice(double) double
  + value(double) double
  + freeItems() List~MenuItem~
   boolean stackable
   boolean expired
}
class DiscountBuilder {
  + appliesAfterOrder() DiscountBuilder
  + build() Discount
  + appliesDuringOrder() DiscountBuilder
  + stackable() DiscountBuilder
  + neverExpires() DiscountBuilder
  + unstackable() DiscountBuilder
  + oneTimeOffer() DiscountBuilder
  + expiresAt(LocalDateTime) DiscountBuilder
   MenuItem[] freeItems
   double orderDiscount
   Criteria criteria
   int ordersAmount
   Role[] userRoles
   Discounts discounts
   double orderCredit
   Options options
   int currentOrderItemsAmount
   int itemsAmount
}
class GroupOrder {
  + pay(SingleOrder) boolean
  + createOrder(User) SingleOrder
  + getAvailableDeliveryTimes(LocalDateTime, int) List~LocalDateTime~
  + getAvailableMenu(LocalDateTime) List~MenuItem~
  + closeOrder() void
   LocalDateTime orderTime
   double price
   List~SingleOrder~ orders
   List~User~ users
   Restaurant restaurant
   Duration preparationTime
   String groupCode
   Address address
   LocalDateTime deliveryTime
   String restaurantId
   List~MenuItem~ items
   Status status
}
class GroupOrderManager {
   GroupOrderManager INSTANCE
}
class MenuItem {
  + equals(Object) boolean
  + hashCode() int
  + toString() String
   String name
   Restaurant restaurant
   Duration preparationTime
   double price
   String restaurantName
}
class NotFoundException
class OpeningTime {
  + equals(Object) boolean
  + hashCode() int
  + toString() String
   LocalTime end
   LocalTime start
}
class Order {
<<Interface>>
  + getAvailableMenu(LocalDateTime) List~MenuItem~
   LocalDateTime orderTime
   List~User~ users
   Restaurant restaurant
   Duration preparationTime
   String groupCode
   Address address
   LocalDateTime deliveryTime
   String restaurantId
   List~MenuItem~ items
   Status status
}
class Payment {
  + amount() Double
  + date() LocalDateTime
}
class PaymentSystem {
  + pay(double) Optional~Payment~
}
class Restaurant {
  - getMaxCapacityLeft(LocalDateTime) Duration
  + getAvailableMenu(LocalDateTime) List~MenuItem~
  - canAddOrder(LocalDateTime, Duration) boolean
  + removeMenuItem(MenuItem) void
  - capacityLeft(Schedule, LocalDateTime) Duration
  + addOrder(Order) void
  + availableDiscounts(SingleOrder) List~Discount~
  + equals(Object) boolean
  + canDeliverAt(LocalDateTime) boolean
  + removeDiscount(Discount) void
  + addSchedule(Schedule) void
  + discounts() List~Discount~
  + addMenuItem(MenuItem) void
  + toString() String
  + hashCode() int
  + canHandle(Order, LocalDateTime) boolean
  + getOpeningTimes(DayOfWeek) List~OpeningTime~
  + addDiscount(Discount) void
   String name
   TypeOfFood typeOfFood
   List~Order~ orders
   List~MenuItem~ fullMenu
   Duration averagePreparationTime
   Duration scheduleDuration
}
class RestaurantManager {
  + filterRestaurant(LocalDateTime) List~Restaurant~
  + filterRestaurant(String, TypeOfFood, LocalDateTime) List~Restaurant~
  + filterRestaurant(String) List~Restaurant~
  + filterRestaurant(TypeOfFood) List~Restaurant~
   RestaurantManager INSTANCE
}
class Role {
<<enumeration>>
  + values() Role[]
  + valueOf(String) Role
}
class STEats {
  - updateFullMenu(Order) void
  + joinGroupOrder(String) void
  + addMenuItem(MenuItem) void
  + payOrder() boolean
  + closeGroupOrder() void
  + createGroupOrder(LocalDateTime, String, String) String
  + removeMenuItem(MenuItem) void
  + getOpeningTimes(Restaurant) Map~DayOfWeek, List~OpeningTime~~
  + canCloseGroupOrder() boolean
  + changeDeliveryTime(LocalDateTime) void
  + createOrder(LocalDateTime, String, String) void
  + getAvailableDeliveryTimes(LocalDateTime, int) List~LocalDateTime~
   List~MenuItem~ availableMenu
   SingleOrder order
   List~Restaurant~ allRestaurants
   List~MenuItem~ fullMenu
   List~MenuItem~ cart
   String groupCode
   double totalPrice
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
  + contains(Order) boolean
  + compareTo(LocalDateTime) int
  + hashCode() int
  + contains(LocalDateTime) boolean
  + isBetween(LocalDateTime, LocalDateTime) boolean
  + compareTo(Schedule) int
  + toString() String
  + equals(Object) boolean
   Duration totalCapacity
   Duration duration
   int nbPerson
   LocalTime end
   DayOfWeek dayOfWeek
   LocalTime start
}
class SingleOrder {
  + addMenuItem(MenuItem) void
  - updateDiscounts() void
  + pay() boolean
  + removeMenuItem(MenuItem) void
  + getAvailableMenu(LocalDateTime) List~MenuItem~
   Optional~GroupOrder~ groupOrder
   double subPrice
   List~Discount~ discounts
   Address address
   String restaurantId
   String id
   User user
   LocalDateTime orderTime
   double price
   List~User~ users
   Restaurant restaurant
   Duration preparationTime
   List~Discount~ discountsToApplyNext
   String groupCode
   LocalDateTime deliveryTime
   List~MenuItem~ items
   Payment payment
   Status status
   String userId
}
class SingleOrderManager {
  + getOrdersByGroup(String) List~SingleOrder~
  + getOrdersByUser(String) List~SingleOrder~
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
  + getDiscountsToApplyNext(String) List~Discount~
  + getOrders(String) List~SingleOrder~
   String name
   List~SingleOrder~ orders
   Role role
   List~Payment~ payments
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
GroupOrder  ..>  SingleOrder : «create»
GroupOrder "1" *--> "status 1" Status 
GroupOrderManager  -->  AbstractManager~T~ 
MenuItem  ..>  Saleable 
Order  -->  Saleable 
PaymentSystem  ..>  Payment : «create»
Restaurant "1" *--> "discounts *" Discount 
Restaurant "1" *--> "menu *" MenuItem 
Restaurant  ..>  OpeningTime : «create»
Restaurant "1" *--> "orders *" Order 
Restaurant "1" *--> "schedules *" Schedule 
Restaurant "1" *--> "typeOfFood 1" TypeOfFood 
RestaurantManager  -->  AbstractManager~T~ 
STEats  ..>  GroupOrder : «create»
STEats "1" *--> "fullMenu *" MenuItem 
STEats "1" *--> "order 1" SingleOrder 
STEats  ..>  SingleOrder : «create»
STEats "1" *--> "user 1" User 
STEatsController  ..>  STEats : «create»
SingleOrder "1" *--> "appliedDiscounts *" Discount 
SingleOrder "1" *--> "items *" MenuItem 
SingleOrder  ..>  Order 
SingleOrder "1" *--> "payment 1" Payment 
SingleOrder "1" *--> "status 1" Status 
SingleOrderManager  -->  AbstractManager~T~ 
User "1" *--> "role 1" Role 
UserManager  -->  AbstractManager~T~ 
UserManager  ..>  User : «create»
```

# Sequence Diagram

## Individual order inside a group order

```mermaid
sequenceDiagram
    actor Client
    Client ->> STEats: joinGroupOrder(groupCode)
    activate STEats
    STEats ->> GroupOrderManager: <<static>><br />getInstance()
    activate GroupOrderManager
    GroupOrderManager -->> STEats: GroupOrderManager
    STEats ->> GroupOrderManager: get(groupCode)
    GroupOrderManager -->> STEats: GroupOrder
    deactivate GroupOrderManager
    STEats ->> GroupOrder: createOrder(userId)
    activate GroupOrder
    create participant SingleOrder
    GroupOrder ->> SingleOrder: <<create>>
    activate SingleOrder
    SingleOrder ->> SingleOrderManager: <<static>><br />getInstance()
    activate SingleOrderManager
    SingleOrderManager -->> SingleOrder: SingleOrderManager
    SingleOrder ->> SingleOrderManager: add(singleOrderId, this)
    SingleOrderManager -->> SingleOrder: #32;
    deactivate SingleOrderManager
    SingleOrder -->> GroupOrder: SingleOrder
    deactivate SingleOrder
    GroupOrder -->> STEats: SingleOrder
    deactivate GroupOrder
    STEats ->> STEats: updateFullMenu()
    STEats ->> Order: getRestaurant()
    activate Order
    Order -->> STEats: Restaurant
    deactivate Order
    STEats ->> Restaurant: getFullMenu()
    activate Restaurant
    Restaurant -->> STEats: #32;
    deactivate Restaurant
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

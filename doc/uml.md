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
        + clear() void
        + add(String, T) void
        + contains(String) boolean
        + remove(String) void
        + get(String) T
        List~T~ all
    }
    class Address {
        + label() String
        + street() String
        + additional_address() String
        + postal_code() String
        + city() String
    }
    class AddressManager {
        AddressManager INSTANCE
    }
    class Discount {
        + isApplicable(SingleOrder) boolean
        + value(double) double
        + freeItems() List~MenuItem~
        + getNewPrice(double) double
        + canBeAppliedDirectly() boolean
        boolean stackable
        boolean expired
    }
    class DiscountBuilder {
        + stackable() DiscountBuilder
        + oneTimeOffer() DiscountBuilder
        + neverExpires() DiscountBuilder
        + unstackable() DiscountBuilder
        + appliesAfterOrder() DiscountBuilder
        + expiresAt(LocalDateTime) DiscountBuilder
        + appliesDuringOrder() DiscountBuilder
        + build() Discount
        MenuItem[] freeItems
        int ordersAmount
        Criteria criteria
        Role[] userRoles
        Discounts discounts
        Options options
        double orderDiscount
        int itemsAmount
        int currentOrderItemsAmount
        double orderCredit
    }
    class GroupOrder {
        + closeOrder() void
        + pay(SingleOrder) boolean
        + getAvailableDeliveryTimes(LocalDateTime, int) List~LocalDateTime~
        + createOrder(String) SingleOrder
        List~MenuItem~ availableMenu
        List~SingleOrder~ orders
        Address address
        String restaurantId
        LocalDateTime orderTime
        double price
        List~User~ users
        Restaurant restaurant
        Duration preparationTime
        String groupCode
        LocalDateTime deliveryTime
        List~MenuItem~ items
        Status status
    }
    class GroupOrderManager {
        GroupOrderManager INSTANCE
    }
    class MenuItem {
        + toString() String
        + equals(Object) boolean
        + hashCode() int
        String name
        Restaurant restaurant
        Duration preparationTime
        String restaurantName
        double price
    }
    class NotFoundException
    class OpeningTime {
        + toString() String
        + hashCode() int
        + equals(Object) boolean
        LocalTime end
        LocalTime start
    }
    class Order {
        <<Interface>>
        LocalDateTime orderTime
        List~MenuItem~ availableMenu
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
        + date() LocalDateTime
        + amount() Double
    }
    class PaymentSystem {
        + pay(double) Optional~Payment~
    }
    class Restaurant {
        - getMaxCapacityLeft(LocalDateTime) Duration
        + hashCode() int
        + addSchedule(Schedule) void
        + removeDiscount(Discount) void
        + addOrder(Order) void
        + addMenuItem(MenuItem) void
        + discounts() List~Discount~
        - capacityLeft(Schedule, LocalDateTime) Duration
        + toString() String
        - canAddOrder(LocalDateTime, Duration) boolean
        + getOpeningTimes(DayOfWeek) List~OpeningTime~
        + getAvailableMenu(LocalDateTime) List~MenuItem~
        + equals(Object) boolean
        + addScheduleForPeriod(int, DayOfWeek, LocalTime, DayOfWeek, LocalTime) void
        + availableDiscounts(SingleOrder) List~Discount~
        + canHandle(Order, LocalDateTime) boolean
        + canDeliverAt(LocalDateTime) boolean
        + addDiscount(Discount) void
        + removeMenuItem(MenuItem) void
        String name
        TypeOfFood typeOfFood
        List~Order~ orders
        List~MenuItem~ fullMenu
        Duration averagePreparationTime
        Duration scheduleDuration
    }
    class RestaurantManager {
        + filterRestaurant(LocalDateTime) List~Restaurant~
        + filterRestaurant(String) List~Restaurant~
        + filterRestaurant(String, TypeOfFood, LocalDateTime) List~Restaurant~
        + filterRestaurant(TypeOfFood) List~Restaurant~
        RestaurantManager INSTANCE
    }
    class Role {
        <<enumeration>>
        + valueOf(String) Role
        + values() Role[]
    }
    class STEats {
        + getOpeningTimes(Restaurant) Map~DayOfWeek, List~ OpeningTime~~
        + changeDeliveryTime(LocalDateTime) void
        + closeGroupOrder() void
        + getAvailableDeliveryTimes(LocalDateTime, int) List~LocalDateTime~
        + removeMenuItem(MenuItem) void
        + canCloseGroupOrder() boolean
        + payOrder() boolean
        + addMenuItem(MenuItem) void
        + joinGroupOrder(String) void
        - updateFullMenu() void
        + createGroupOrder(LocalDateTime, String, String) String
        + createOrder(LocalDateTime, String, String) void
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
        + compareTo(LocalDateTime) int
        + isBetween(LocalDateTime, LocalDateTime) boolean
        + contains(LocalDateTime) boolean
        + hashCode() int
        + toString() String
        + equals(Object) boolean
        + contains(Order) boolean
        + compareTo(Schedule) int
        Duration totalCapacity
        int nbPerson
        Duration duration
        LocalTime end
        DayOfWeek dayOfWeek
        LocalTime start
    }
    class SingleOrder {
        + addMenuItem(MenuItem) void
        + removeMenuItem(MenuItem) void
        + pay() boolean
        - updateDiscounts() void
        Optional~GroupOrder~ groupOrder
        List~MenuItem~ availableMenu
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
        + getOrdersByUser(String) List~SingleOrder~
        + getOrdersByGroup(String) List~SingleOrder~
        SingleOrderManager INSTANCE
    }
    class Status {
        <<enumeration>>
        + values() Status[]
        + valueOf(String) Status
    }
    class TypeOfFood {
        <<enumeration>>
        + values() TypeOfFood[]
        + valueOf(String) TypeOfFood
    }
    class User {
        + getOrders(String) List~SingleOrder~
        + getDiscountsToApplyNext(String) List~Discount~
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

    AbstractManager~T~ ..> NotFoundException: «create»
    AddressManager --> AbstractManager~T~
    DiscountBuilder ..> Discount: «create»
    GroupOrder ..> Order
    GroupOrder ..> SingleOrder: «create»
    GroupOrder "1" *--> "status 1" Status
    GroupOrderManager --> AbstractManager~T~
    MenuItem ..> Saleable
    Order --> Saleable
    PaymentSystem ..> Payment: «create»
    Restaurant "1" *--> "discounts *" Discount
    Restaurant "1" *--> "menu *" MenuItem
    Restaurant ..> OpeningTime: «create»
    Restaurant "1" *--> "orders *" Order
    Restaurant ..> Schedule: «create»
    Restaurant "1" *--> "schedules *" Schedule
    Restaurant "1" *--> "typeOfFood 1" TypeOfFood
    RestaurantManager --> AbstractManager~T~
    STEats ..> GroupOrder: «create»
    STEats "1" *--> "fullMenu *" MenuItem
    STEats "1" *--> "order 1" SingleOrder
    STEats ..> SingleOrder: «create»
    STEats "1" *--> "user 1" User
    STEatsController ..> STEats: «create»
    SingleOrder "1" *--> "appliedDiscounts *" Discount
    SingleOrder "1" *--> "items *" MenuItem
    SingleOrder ..> Order
    SingleOrder "1" *--> "payment 1" Payment
    SingleOrder "1" *--> "status 1" Status
    SingleOrderManager --> AbstractManager~T~
    User "1" *--> "role 1" Role
    UserManager --> AbstractManager~T~
    UserManager ..> User: «create»
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
    SingleOrder ->> Restaurant: canHandle(order, deliveryTime)
    activate Restaurant
    Restaurant ->> Restaurant: getMaxCapacityLeft(deliveryTime)
    loop For each schedule
        Restaurant ->> Schedule: isBetween(deliveryTime - 2h, deliveryTime)
        activate Schedule
        note right of Schedule: Calls inside isBetween<br/>are not shown here for clarity
        Schedule -->> Restaurant: boolean
        deactivate Schedule
    end
    note right of Restaurant: Keep only schedules that are at<br/>most 2 hours before the delivery time
    loop For each kept schedule
        Restaurant ->> Restaurant: capacityLeft(schedule, deliveryDate)
        loop For each, at least paid, order of the schedule at the given date
            Restaurant ->> Order: getPreparationTime()
            activate Order
            Order -->> Restaurant: Duration
            deactivate Order
            note right of Restaurant: Durations are summed up
        end
        Restaurant ->> Schedule: getTotalCapacity()
        activate Schedule
        Schedule -->> Restaurant: Duration
        deactivate Schedule
        note right of Restaurant: Capacity is obtained by<br/>substracting the sum of the preparation<br/>times from the total capacity
    end
    note right of Restaurant: Keep the max obtained capacity
    Restaurant ->> Restaurant: canAddOrder(deliveryTime, maxCapacity)
    Restaurant ->> Restaurant: getAveragePreparationTime()
    loop For each last 50 orders
        Restaurant ->> Order: getPreparationTime()
        activate Order
        Order -->> Restaurant: Duration
        deactivate Order
        note right of Restaurant: Averages durations
    end
    note right of Restaurant: Check if the current number of orders<br/>is not too high compared to the average<br/>preparation time
    Restaurant -->> SingleOrder: boolean
    deactivate Restaurant
    SingleOrder ->> SingleOrderManager: <<static>><br />getInstance()
    activate SingleOrderManager
    SingleOrderManager -->> SingleOrder: SingleOrderManager
    SingleOrder ->> SingleOrderManager: add(singleOrderId, this)
    SingleOrderManager -->> SingleOrder: #32;
    deactivate SingleOrderManager
    SingleOrder -->> GroupOrder: SingleOrder
    GroupOrder -->> STEats: SingleOrder
    deactivate GroupOrder
    STEats ->> STEats: updateFullMenu()
    STEats ->> Restaurant: getFullMenu()
    activate Restaurant
    Restaurant -->> STEats: List<MenuItem>
    deactivate Restaurant
    Client ->> STEats: getAvailableMenu()
    STEats ->> SingleOrder: getAvailableMenu()
    SingleOrder ->> Restaurant: getAvailableMenu(deliveryTime)
    activate Restaurant
    Restaurant ->> Restaurant: getMaxCapacityLeft(deliveryTime)
    loop For each schedule
        Restaurant ->> Schedule: isBetween(deliveryTime, deliveryTime - 2h)
        activate Schedule
        note right of Schedule: Calls inside isBetween<br/>are not shown here for clarity
        Schedule -->> Restaurant: boolean
        deactivate Schedule
    end
    note right of Restaurant: Only schedules that<br/>are at most 2 hours before<br/>the delivery time are considered
    loop For each kept schedule
    Restaurant ->> Restaurant: capacityLeft(schedule, deliveryTime)
    note right of Restaurant: Calls inside capacityLeft<br/>are not shown here for clarity
        loop For each item ordered during the schedule
    Restaurant ->> MenuItem: getPreparationTime()
    activate MenuItem
    MenuItem -->> Restaurant: Duration
    deactivate MenuItem
        end
    end
    Restaurant -->> SingleOrder: List<MenuItem>
    deactivate Restaurant
    SingleOrder -->> STEats: List<MenuItem>
    Client ->> STEats: addMenuItem(menuItem)
    STEats ->> SingleOrder: addMenuItem(menuItem)
    SingleOrder ->> SingleOrder: updateDiscounts()
    note right of SingleOrder: updateDiscounts is represented<br/>in another chart for clarity
    deactivate SingleOrder
    deactivate STEats
```

## Discounts

```mermaid
sequenceDiagram
    activate SingleOrder
    SingleOrder ->> SingleOrder: updateDiscounts()
    SingleOrder ->> Restaurant: availableDiscounts(order)
    activate Restaurant
    loop For each discount of the restaurant
        Restaurant ->> Discount: isApplicable(order)
        activate Discount
        Discount ->> SingleOrder: getItems()
        SingleOrder -->> Discount: List<MenuItem>
        Discount ->> User: getOrders(restaurantId)
        activate User
        note right of User: Returns orders that<br/>are at least paid<br/>and placed at<br/>the same restaurant
        User -->> Discount: List<SingleOrder>
        deactivate User
        note right of Discount: Check criteria like number of items<br/>in cart and amount of orders
        loop For each returned order
            Discount ->> SingleOrder: getItems()
            SingleOrder -->> Discount: List<MenuItem>
            note right of Discount: Used to count the<br/>total number of ordered items
        end
        Discount ->> User: getRole
        activate User
        User -->> Discount: Role
        deactivate User
        note right of Discount: Check the role criteria
        Discount -->> Restaurant: boolean
        deactivate Discount
    end
    loop For each applicable discount
        Restaurant ->> Discount: isStackable()
        activate Discount
        Discount -->> Restaurant: boolean
        deactivate Discount
        note right of Restaurant: every stackable discounts will be applied
    end
    loop For each un-stackable discount
        Restaurant ->> SingleOrder: getSubPrice()
        loop For each menu item in the order
            SingleOrder ->> MenuItem: getPrice()
            activate MenuItem
            MenuItem -->> SingleOrder: double
            deactivate MenuItem
        end
        SingleOrder -->> Restaurant: double
        Restaurant ->> Discount: value(subPrice)
        activate Discount
        note right of Discount: Calculates the new price<br/>after applying the discount
        Discount -->> Restaurant: double
        deactivate Discount
        note right of Restaurant: Only the best un-stackable discount is applied
    end
    Restaurant -->> SingleOrder: List<Discount>
    deactivate Restaurant
    deactivate SingleOrder
```

# Mockup

![Mockup](mockup.jpg)

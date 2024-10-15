package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.order.SingleOrder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A restaurant that serves food
 *
 * @author Team C
 */
public class Restaurant {
    private final String name;
    private final List<MenuItem> menu;
    private final TypeOfFood typeOfFood;
    private final List<Discount> discounts;
    private final List<Order> orders;
    private final List<Schedule> schedules = new ArrayList<>();
    private final static Duration DELIVERY_TIME_RESTAURANT = Duration.ofMinutes(10);

    public Restaurant(String name, List<MenuItem> menu, TypeOfFood typeOfFood, List<Discount> discounts) {
        this.name = name;
        this.menu = menu;
        this.typeOfFood = typeOfFood;
        this.discounts = discounts;
        this.orders = new ArrayList<>();
    }

    public Restaurant(String name) {
        this(name, new ArrayList<>(), TypeOfFood.CLASSIC, new ArrayList<>());
    }

    /**
     * Name of the restaurant
     */
    public String getName() {
        return name;
    }

    /**
     * The full menu of the restaurant
     */
    public List<MenuItem> getFullMenu() {
        return Collections.unmodifiableList(menu);
    }

    /**
     * The type of food the restaurant serves
     */
    public TypeOfFood getTypeOfFood() {
        return typeOfFood;
    }

    /**
     * All the discounts the restaurant proposes
     */
    public List<Discount> discounts() {
        return Collections.unmodifiableList(discounts);
    }

    /**
     * All the orders received by the restaurant
     */
    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * The discounts that can be applied to an order
     *
     * @param order The order to check
     */
    public List<Discount> availableDiscounts(SingleOrder order) {
        List<Discount> applicableDiscounts = discounts().stream().filter(discount -> discount.isApplicable(order)).toList();
        List<Discount> res = new ArrayList<>(applicableDiscounts.stream().filter(Discount::isStackable).toList());
        applicableDiscounts.stream()
                .filter(discount -> !discount.isStackable())
                .max(Comparator.comparingDouble(discount -> discount.value(order.getSubPrice())))
                .ifPresent(res::add);
        return res;
    }

    /**
     * The part of the menu that can be prepared and delivered in time
     *
     * @param deliveryTimeOrder Wanted time of delivery
     */
    public List<MenuItem> getAvailableMenu(LocalDateTime deliveryTimeOrder) {
        Optional<Schedule> scheduleOptional = schedules.stream().filter(schedule -> schedule.contains(deliveryTimeOrder.minus(DELIVERY_TIME_RESTAURANT))).findFirst();
        if (scheduleOptional.isEmpty())
            throw new IllegalArgumentException("This restaurant can't deliver at this time");
        Schedule schedule = scheduleOptional.get();
        List<Order> ordersTakenAccountSchedule = orders.stream().filter(order -> schedule.contains(order.getDeliveryTime())).toList();
        Duration totalPreparationTimeOrders = ordersTakenAccountSchedule.stream().map(Order::getPreparationTime).reduce(Duration.ZERO, Duration::plus);
        Duration capacityLeft = schedule.getCapacity().minus(totalPreparationTimeOrders);
        return menu.stream().filter(menuItem -> !capacityLeft.minus(menuItem.getPreparationTime()).isNegative()).toList();
    }

    /**
     * Add a menu item to the restaurant
     *
     * @param menuItem The menu item
     */
    public void addMenuItem(MenuItem menuItem) {
        this.menu.add(menuItem);
    }

    /**
     * Remove a menu item to the restaurant
     *
     * @param menuItem The menu item
     */
    public void removeMenuItem(MenuItem menuItem) {
        this.menu.remove(menuItem);
    }

    /**
     * Add an order for the restaurant
     *
     * @param order the order to add
     */
    public void addOrder(Order order) {
        this.orders.add(order);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Restaurant) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.menu, that.menu) &&
                Objects.equals(this.typeOfFood, that.typeOfFood) &&
                Objects.equals(this.discounts, that.discounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, menu, typeOfFood, discounts);
    }

    @Override
    public String toString() {
        return name + " [" + typeOfFood + "]";
    }

    /**
     * Add a schedule to the restaurant
     *
     * @param schedule The schedule to add
     */
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }
}

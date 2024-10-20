package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.user.NotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A restaurant that serves food
 *
 * @author Team C
 */
public class Restaurant {
    private final String name;
    private final TypeOfFood typeOfFood;
    private final Duration scheduleDuration;
    private final List<MenuItem> menu = new ArrayList<>();
    private final List<Discount> discounts = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final Set<Schedule> schedules = new HashSet<>();
    private final static Duration MAX_PREPARATION_DURATION_BEFORE_DELIVERY = Duration.ofHours(2);
    private final static Duration DELIVERY_TIME_RESTAURANT = Duration.ofMinutes(10);

    public Restaurant(String name, TypeOfFood typeOfFood, Duration scheduleDuration) {
        this.name = name;
        this.typeOfFood = typeOfFood;
        this.scheduleDuration = scheduleDuration;
    }

    public Restaurant(String name) {
        this(name, TypeOfFood.CLASSIC);
    }

    public Restaurant(String name, TypeOfFood typeOfFood) {
        this(name, typeOfFood, Duration.ofMinutes(30));
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
        List<Discount> applicableDiscounts = discounts().stream().filter(discount -> {
            try {
                return discount.isApplicable(order);
            } catch (NotFoundException e) {
                return false;
            }
        }).toList();
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
     * @param arrivalTime Wanted time of delivery
     */
    public List<MenuItem> getAvailableMenu(LocalDateTime arrivalTime) {
        if (arrivalTime == null) return menu;
        Duration maxCapacity = getMaxCapacityLeft(arrivalTime);
        return menu.stream().filter(menuItem -> !maxCapacity.minus(menuItem.getPreparationTime()).isNegative()).toList();
    }

    /**
     * Check if the restaurant can handle an order at a given time
     *
     * @param order        The order to check
     * @param deliveryTime The time of delivery
     */
    public boolean canHandle(Order order, LocalDateTime deliveryTime) {
        Duration maxCapacity = getMaxCapacityLeft(deliveryTime);
        return maxCapacity.compareTo(order.getPreparationTime()) >= 0;
    }

    private Duration capacityLeft(Schedule schedule, LocalDateTime deliveryTimeOrder) {
        List<Order> ordersTakenAccountSchedule = orders.stream()
                .filter(order -> order.getDeliveryTime().getDayOfYear() == deliveryTimeOrder.getDayOfYear())
                .filter(schedule::contains)
                .toList();
        Duration totalPreparationTimeOrders = ordersTakenAccountSchedule.stream()
                .map(Order::getPreparationTime)
                .reduce(Duration.ZERO, Duration::plus);
        return schedule.getTotalCapacity().minus(totalPreparationTimeOrders);
    }

    private Duration getMaxCapacityLeft(LocalDateTime arrivalTime) {
        LocalDateTime deliveryTime = arrivalTime.minus(DELIVERY_TIME_RESTAURANT);
        Set<Schedule> schedulesBefore2Hours = schedules.stream()
                .filter(schedule -> schedule.isBetween(deliveryTime, deliveryTime.minus(MAX_PREPARATION_DURATION_BEFORE_DELIVERY)))
                .collect(Collectors.toSet());
        return schedulesBefore2Hours.stream()
                .map(schedule -> capacityLeft(schedule, deliveryTime))
                .max(Comparator.comparing(Function.identity()))
                .orElseThrow(() -> new IllegalArgumentException("This restaurant can't deliver at this time"));
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
     * Add a discount to the restaurant
     *
     * @param discount The discount
     */
    public void addDiscount(Discount discount) {
        this.discounts.add(discount);
    }

    /**
     * Remove a discount of the restaurant
     *
     * @param discount The discount
     */
    public void removeDiscount(Discount discount) {
        this.discounts.remove(discount);
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
        if (!schedule.getDuration().equals(scheduleDuration))
            throw new IllegalArgumentException("This schedule's duration does not coincide with the restaurant' schedule duration");
        if (schedules.stream().anyMatch(s -> s.overlap(schedule)))
            throw new IllegalArgumentException("This schedule overlaps with another schedule of the restaurant");
        schedules.add(schedule);
    }
}

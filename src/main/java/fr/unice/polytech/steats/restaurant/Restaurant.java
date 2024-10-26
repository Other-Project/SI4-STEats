package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.order.Status;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final Set<Schedule> schedules = new TreeSet<>();
    private static final Duration MAX_PREPARATION_DURATION_BEFORE_DELIVERY = Duration.ofHours(2);
    private static final Duration DELIVERY_TIME_RESTAURANT = Duration.ofMinutes(10);

    private static final int RELEVANT_NUMBER_OF_ORDER_FOR_MEAN_CALCULATION = 50;

    /**
     * Create a restaurant
     *
     * @param name             The name of the restaurant
     * @param typeOfFood       The type of food the restaurant serves
     * @param scheduleDuration The duration of the schedule
     */
    public Restaurant(String name, TypeOfFood typeOfFood, Duration scheduleDuration) {
        this.name = name;
        this.typeOfFood = typeOfFood;
        this.scheduleDuration = scheduleDuration;
    }

    /**
     * Create a restaurant
     *
     * @param name The name of the restaurant
     */
    public Restaurant(String name) {
        this(name, TypeOfFood.CLASSIC);
    }

    /**
     * Create a restaurant
     *
     * @param name       The name of the restaurant
     * @param typeOfFood The type of food the restaurant serves
     */
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
     * The duration of each schedule
     */
    public Duration getScheduleDuration() {
        return scheduleDuration;
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
        return maxCapacity.compareTo(order.getPreparationTime()) >= 0 && canAddOrder(order.getDeliveryTime(), maxCapacity);
    }

    private boolean canAddOrder(LocalDateTime deliveryTime, Duration maxCapacity) {
        if (deliveryTime == null || orders.isEmpty()) return true;
        long maxNbOfOrder = maxCapacity.toMinutes() / getAveragePreparationTime().toMinutes();
        long currentNbOfOrder = orders.stream()
                .filter(order -> order.getStatus() == Status.INITIALISED)
                .count();
        return currentNbOfOrder < maxNbOfOrder;
    }

    private Duration getAveragePreparationTime() {
        if (orders.isEmpty()) return Duration.ZERO;
        return orders.reversed().stream()
                .limit(RELEVANT_NUMBER_OF_ORDER_FOR_MEAN_CALCULATION)
                .map(Order::getPreparationTime)
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(Math.min(RELEVANT_NUMBER_OF_ORDER_FOR_MEAN_CALCULATION, orders.size()));
    }

    /**
     * Check if the restaurant can deliver at a given time
     *
     * @param deliveryTime The time of delivery
     * @return True if the restaurant can deliver at the given time, false otherwise
     */
    public boolean canDeliverAt(LocalDateTime deliveryTime) {
        try {
            Duration maxCapacity = getMaxCapacityLeft(deliveryTime);
            return menu.stream().anyMatch(menuItem -> maxCapacity.compareTo(menuItem.getPreparationTime()) >= 0);
        } catch (Exception e) {
            return false;
        }
    }

    private Duration capacityLeft(Schedule schedule, LocalDateTime deliveryTimeOrder) {
        List<Order> ordersTakenAccountSchedule = orders.stream()
                .filter(order -> order.getStatus().compareTo(Status.PAID) > 0 || (order.getStatus() == Status.PAID && order.getDeliveryTime() != null))
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
        menuItem.setRestaurantName(name);
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
     * Get the opening times of the restaurant for a given day
     *
     * @param day The day of the week
     */
    public List<OpeningTime> getOpeningTimes(DayOfWeek day) {
        List<Schedule> scheduleList = schedules.stream()
                .filter(schedule -> schedule.getDayOfWeek() == day)
                .sorted(Comparator.comparing(Schedule::getStart))
                .toList();
        List<OpeningTime> intervals = new ArrayList<>();
        OpeningTime currentInterval = null;
        for (Schedule schedule : scheduleList) {
            if (currentInterval != null && currentInterval.getEnd().equals(schedule.getStart())) {
                currentInterval.setEnd(schedule.getEnd());
                continue;
            } else if (currentInterval != null) intervals.add(currentInterval);
            currentInterval = new OpeningTime(schedule.getStart(), schedule.getEnd());
        }
        if (currentInterval != null && currentInterval.getEnd().equals(LocalTime.of(0, 0))) currentInterval.setEnd(LocalTime.of(23, 59, 59));
        if (currentInterval != null) intervals.add(currentInterval);
        return intervals;
    }

    /**
     * Add an order for the restaurant
     *
     * @param order the order to add
     */
    public void addOrder(Order order) {
        this.orders.add(order);
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

    /**
     * Add schedules for a period of time
     *
     * @param nbPersons The number of working persons for the schedule
     * @param startDay  The day of the week to start the period
     * @param startTime The time to start the period
     * @param endDay    The day of the week to end the period
     * @param endTime   The time to end the period
     */
    public void addScheduleForPeriod(int nbPersons, DayOfWeek startDay, LocalTime startTime, DayOfWeek endDay, LocalTime endTime) {
        DayOfWeek day = startDay;
        for (LocalTime time = startTime; day != endDay || time.isBefore(endTime); time = time.plus(getScheduleDuration())) {
            addSchedule(new Schedule(time, getScheduleDuration(), nbPersons, day));
            if (time.isAfter(LocalTime.MAX.minus(getScheduleDuration())) && !time.plus(getScheduleDuration()).isAfter(LocalTime.MIDNIGHT.plus(getScheduleDuration())))
                day = day.plus(1);
        }
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
}

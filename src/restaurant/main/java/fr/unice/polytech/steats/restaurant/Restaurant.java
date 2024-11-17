package fr.unice.polytech.steats.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.helpers.MenuItemServiceHelper;
import fr.unice.polytech.steats.helpers.ScheduleServiceHelper;
import fr.unice.polytech.steats.models.MenuItem;
import fr.unice.polytech.steats.models.Order;
import fr.unice.polytech.steats.models.Schedule;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.function.Function;

/**
 * A restaurant that serves food
 *
 * @author Team C
 */
public class Restaurant {
    private final String id;
    private final String name;
    private final TypeOfFood typeOfFood;
    private final Duration scheduleDuration;
    // private final List<MenuItem> menu = new ArrayList<>();
    // private final List<Discount> discounts = new ArrayList<>();
    // private final List<Order> orders = new ArrayList<>();
    // private final Set<Schedule> schedules = new TreeSet<>();
    private static final Duration MAX_PREPARATION_DURATION_BEFORE_DELIVERY = Duration.ofHours(2);
    private static final Duration DELIVERY_TIME_RESTAURANT = Duration.ofMinutes(10);

    private static final int RELEVANT_NUMBER_OF_ORDER_FOR_MEAN_CALCULATION = 50;

    /**
     * Create a restaurant
     *
     * @param id               The id of the restaurant
     * @param name             The name of the restaurant
     * @param typeOfFood       The type of food the restaurant serves
     * @param scheduleDuration The duration of the schedule
     */
    public Restaurant(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("typeOfFood") TypeOfFood typeOfFood, @JsonProperty("scheduleDuration") Duration scheduleDuration) {
        this.id = id;
        this.name = name;
        this.typeOfFood = typeOfFood;
        this.scheduleDuration = scheduleDuration;
    }

    /**
     * Create a restaurant
     *
     * @param name The name of the restaurant
     */
    public Restaurant(String id, String name) {
        this(id, name, TypeOfFood.CLASSIC);
    }

    /**
     * Create a restaurant
     *
     * @param name       The name of the restaurant
     * @param typeOfFood The type of food the restaurant serves
     */
    public Restaurant(String id, String name, TypeOfFood typeOfFood) {
        this(id, name, typeOfFood, Duration.ofMinutes(30));
    }

    /**
     * Name of the restaurant
     */
    public String getName() {
        return name;
    }

    /**
     * ID of the Restaurant
     */
    public String getId() {
        return id;
    }

    /**
     * The full menu of the restaurant
     */
    @JsonIgnore
    public List<MenuItem> getFullMenu() throws IOException {
        return MenuItemServiceHelper.getMenuItemByRestaurantId(getId());
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
    public void discounts() {
        // TODO : call the discount micro-service
        // return Collections.unmodifiableList(discounts);
    }

    /**
     * All the orders received by the restaurant
     */
    public void getOrders() {
        // TODO : call the Order Service
        // return new ArrayList<>(orders);
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
    /*
    TODO
    public List<Discount> availableDiscounts(SingleOrder order) {
        *//*
        List<Discount> applicableDiscounts = discounts().stream().filter(discount -> discount.isApplicable(order)).toList();
        List<Discount> res = new ArrayList<>(applicableDiscounts.stream().filter(Discount::isStackable).toList());
        applicableDiscounts.stream()
                .filter(discount -> !discount.isStackable())
                .max(Comparator.comparingDouble(discount -> discount.value(order.getSubPrice())))
                .ifPresent(res::add);

         *//*
        return new ArrayList<>();
    }*/


    /**
     * The part of the menu that can be prepared and delivered in time
     *
     * @param arrivalTime Wanted time of delivery
     */
    public List<MenuItem> getAvailableMenu(LocalDateTime arrivalTime) throws IOException {
        List<MenuItem> menu = MenuItemServiceHelper.getMenuItemByRestaurantId(getId());
        if (arrivalTime == null) return menu;
        Duration maxCapacity = getMaxCapacityLeft(arrivalTime);
        return menu.stream().filter(menuItem -> {
            assert maxCapacity != null;
            return !maxCapacity.minus(menuItem.preparationTime()).isNegative();
        }).toList();
    }

    /**
     * Check if the restaurant can handle an order at a given time
     * <p>
     * //* @param order        The order to check
     *
     * @param deliveryTime The time of delivery
     */
    /*
    TODO
    public boolean canHandle(Order order, LocalDateTime deliveryTime) {
        if (deliveryTime == null) return true;
        Duration maxCapacity = getMaxCapacityLeft(deliveryTime);
        return maxCapacity.compareTo(order.getPreparationTime()) >= 0 && canAddOrder(deliveryTime, maxCapacity);
    }*/
    private boolean canAddOrder(LocalDateTime deliveryTime, Duration maxCapacity) {
        /*
        TODO
        if (deliveryTime == null || orders.isEmpty()) return true;
        long averagePreparationTime = getAveragePreparationTime().toMinutes();
        if (averagePreparationTime == 0) return true;
        long maxNbOfOrder = maxCapacity.toMinutes() / averagePreparationTime;
        long currentNbOfOrder = orders.stream()
                .filter(order -> order.getStatus() == Status.INITIALISED)
                .count();
        return currentNbOfOrder < maxNbOfOrder;

         */
        return true;
    }

    private Duration getAveragePreparationTime() {
        /*
        TODO
        List<Duration> lastOrderDurations = orders.reversed().stream()
                .filter(order -> order.getStatus().compareTo(Status.PAID) >= 0 && order.getDeliveryTime() != null)
                .limit(RELEVANT_NUMBER_OF_ORDER_FOR_MEAN_CALCULATION)
                .map(Order::getPreparationTime)
                .toList();
        if (lastOrderDurations.isEmpty()) return Duration.ZERO;
        return lastOrderDurations.stream()
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(lastOrderDurations.size());

         */
        return null;
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
            return MenuItemServiceHelper.getMenuItemByRestaurantId(id).stream().anyMatch(menuItem -> maxCapacity.compareTo(menuItem.getPreparationTime()) >= 0);
        } catch (Exception e) {
            return false;
        }
    }

    private Duration capacityLeft(Schedule schedule, LocalDate deliveryDate) {
        /*
        List<Order> ordersTakenAccountSchedule = orders.stream()
                .filter(order -> order.getStatus().compareTo(Status.PAID) > 0 || (order.getStatus() == Status.PAID && order.getDeliveryTime() != null))
                .filter(order -> order.getDeliveryTime().getDayOfYear() == deliveryDate.getDayOfYear())
                .filter(schedule::contains)
                .toList();
        Duration totalPreparationTimeOrders = ordersTakenAccountSchedule.stream()
                .map(Order::getPreparationTime)
                .reduce(Duration.ZERO, Duration::plus);
        return schedule.getTotalCapacity().minus(totalPreparationTimeOrders);
         */
        return null;
    }

    private Duration getMaxCapacityLeft(LocalDateTime arrivalTime) throws IOException {
        LocalDateTime deliveryTime = arrivalTime.minus(DELIVERY_TIME_RESTAURANT);
        Set<Schedule> schedulesBefore2Hours = new HashSet<>(ScheduleServiceHelper.getScheduleForDeliveryTime(getId(), deliveryTime, MAX_PREPARATION_DURATION_BEFORE_DELIVERY));
        return schedulesBefore2Hours.stream()
                .map(Schedule::totalCapacity)
                .max(Comparator.comparing(Function.identity()))
                .orElse(Duration.ZERO);
        /*
        Todo : change the return when capacityLeft will work
        return schedulesBefore2Hours.stream()
                .map(schedule -> capacityLeft(schedule, deliveryTime.toLocalDate()))
                .max(Comparator.comparing(Function.identity()))
                .orElse(Duration.ZERO);*/
    }

    /**
     * Add a menu item to the restaurant
     *
     * @param menuItem The menu item
     */
    public void addMenuItem(MenuItem menuItem) {
        // this.menu.add(menuItem);
        // menuItem.setRestaurantName(name);
    }

    /**
     * Remove a menu item to the restaurant
     *
     * @param menuItem The menu item
     */
    public void removeMenuItem(MenuItem menuItem) {
        // this.menu.remove(menuItem);
    }

    /**
     * Add a discount to the restaurant
     *
     * @param discount The discount
     */
    /*public void addDiscount(Discount discount) {
        // this.discounts.add(discount);
    }*/

    /**
     * Remove a discount of the restaurant
     *
     * @param discount The discount
     */
    /*public void removeDiscount(Discount discount) {
        // this.discounts.remove(discount);
    }*/

    /**
     * Get the opening times of the restaurant for a given day
     *
     * @param day The day of the week
     */
    public List<OpeningTime> getOpeningTimes(DayOfWeek day) throws IOException {
        List<Schedule> scheduleList = ScheduleServiceHelper.getScheduleByRestaurantIdAndWeekday(getId(), day);
        List<OpeningTime> intervals = new ArrayList<>();
        OpeningTime currentInterval = null;
        for (Schedule schedule : scheduleList) {
            if (currentInterval != null && currentInterval.getEnd().equals(schedule.start())) {
                currentInterval.setEnd(schedule.end());
                continue;
            } else if (currentInterval != null) intervals.add(currentInterval);
            currentInterval = new OpeningTime(schedule.start(), schedule.end());
        }
        if (currentInterval != null && currentInterval.getEnd().equals(LocalTime.of(0, 0)))
            currentInterval.setEnd(LocalTime.of(23, 59, 59));
        if (currentInterval != null) intervals.add(currentInterval);
        return intervals;
    }

    /**
     * Add an order for the restaurant
     *
     * @param order the order to add
     */
    public void addOrder(Order order) {
        // this.orders.add(order);
    }

    /**
     * Add a schedule to the restaurant
     *
     * @param schedule The schedule to add
     */
    /*public void addSchedule(Schedule schedule) {
     *//*
        if (!schedule.getDuration().equals(scheduleDuration))
            throw new IllegalArgumentException("This schedule's duration does not coincide with the restaurant' schedule duration");
        if (schedules.stream().anyMatch(s -> s.overlap(schedule)))
            throw new IllegalArgumentException("This schedule overlaps with another schedule of the restaurant");
        schedules.add(schedule);

         *//*
    }*/

    /**
     * Add schedules for a period of time
     *
     * @param nbPersons The number of working persons for the schedule
     * @param startDay  The day of the week to start the period
     * @param startTime The time to start the period
     * @param endDay    The day of the week to end the period
     * @param endTime   The time to end the period
     */
    public void addScheduleForPeriod(int nbPersons, DayOfWeek startDay, LocalTime startTime, DayOfWeek endDay, LocalTime endTime) throws IOException {
        DayOfWeek day = startDay;
        long seconds = Math.ceilDiv(startTime.toSecondOfDay(), getScheduleDuration().toSeconds()) * getScheduleDuration().toSeconds();  // round the start time to the nearest schedule
        if (seconds >= 86400) {
            seconds = 0;
            day = day.plus(1);
        }
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        for (; day != endDay || (!time.plus(getScheduleDuration()).isAfter(endTime) && !time.plus(getScheduleDuration()).equals(LocalTime.MIN)); time = time.plus(getScheduleDuration())) {
            ScheduleServiceHelper.addSchedule(new Schedule(UUID.randomUUID().toString(), time, getScheduleDuration(), nbPersons, day, getId(), time.plus(getScheduleDuration()), getScheduleDuration().multipliedBy(nbPersons)));
            if (time.equals(LocalTime.of(0, 0).minus(getScheduleDuration())))
                day = day.plus(1);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Restaurant) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, typeOfFood);
    }

    @Override
    public String toString() {
        return name + " [" + typeOfFood + "]";
    }
}

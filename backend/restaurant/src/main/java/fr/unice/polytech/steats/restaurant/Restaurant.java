package fr.unice.polytech.steats.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.helpers.MenuItemServiceHelper;
import fr.unice.polytech.steats.helpers.OrderServiceHelper;
import fr.unice.polytech.steats.helpers.ScheduleServiceHelper;
import fr.unice.polytech.steats.models.*;

import java.io.IOException;
import java.time.*;
import java.util.*;

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
     * @param id         The id of the restaurant
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
     * The duration of each schedule
     */
    public Duration getScheduleDuration() {
        return scheduleDuration;
    }

    // TODO : Merge this in discount service
//    /**
//     * The discounts that can be applied to an order
//     *
//     * @param order The order to check
//     */
//    public List<Discount> availableDiscounts(SingleOrder order) {
//        List<Discount> applicableDiscounts = discounts().stream().filter(discount -> discount.isApplicable(order)).toList();
//        List<Discount> res = new ArrayList<>(applicableDiscounts.stream().filter(Discount::isStackable).toList());
//        applicableDiscounts.stream()
//                .filter(discount -> !discount.isStackable())
//                .max(Comparator.comparingDouble(discount -> discount.value(order.getSubPrice())))
//                .ifPresent(res::add);
//        return res;
//    }

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
     * Check if the restaurant can handle a quantity of menuItems represented by their total preparation time at a given time
     *
     * @param preparationTime The time it takes to prepare the additional menuItems
     * @param deliveryTime    The time of delivery
     */
    public boolean canHandlePreparationTime(Duration preparationTime, LocalDateTime deliveryTime) throws IOException {
        if (deliveryTime == null) return true;
        return getMaxCapacityLeft(deliveryTime).compareTo(preparationTime) >= 0;
    }

    /**
     * Check if the restaurant add an order at a given time independently of the preparation time
     *
     * @param deliveryTime The time of delivery
     */
    public boolean canAddOrder(LocalDateTime deliveryTime) throws IOException {
        List<Order> orders = OrderServiceHelper.getOrderByRestaurant(id);
        return canAddOrder(deliveryTime, orders);
    }

    /**
     * Check if the restaurant can handle an order at a given time
     *
     * @param preparationTime The time it takes to prepare the additional menuItems
     * @param deliveryTime    The time of delivery
     */
    public boolean canHandle(Duration preparationTime, LocalDateTime deliveryTime) throws IOException {
        List<Schedule> schedulesBefore2Hours = ScheduleServiceHelper.getScheduleForDeliveryTime(getId(), deliveryTime, MAX_PREPARATION_DURATION_BEFORE_DELIVERY).stream()
                .sorted(Comparator.comparing(Schedule::start))
                .toList();
        List<Order> order = OrderServiceHelper.getOrderPastStatus(id, Status.PAID, LocalDateTime.of(deliveryTime.toLocalDate(), schedulesBefore2Hours.getFirst().start()), LocalDateTime.of(deliveryTime.toLocalDate(), schedulesBefore2Hours.getLast().end()));
        return canHandlePreparationTime(preparationTime, deliveryTime, order, schedulesBefore2Hours) && canAddOrder(deliveryTime, order);
    }

    private boolean canHandlePreparationTime(Duration preparationTime, LocalDateTime deliveryTime, List<Order> orders, List<Schedule> schedulesBefore2Hours) {
        if (deliveryTime == null) return true;
        return getMaxCapacityLeft(orders, schedulesBefore2Hours).compareTo(preparationTime) >= 0;
    }

    private Duration getMaxCapacityLeft(List<Order> orders, List<Schedule> schedulesBefore2Hours) {
        Duration maxCapacity = Duration.ZERO;
        for (Schedule schedule : schedulesBefore2Hours) {
            List<Order> ordersInSchedule = orders.stream()
                    .filter(order -> !schedule.start().isAfter(order.deliveryTime().toLocalTime())
                            && schedule.end().isAfter(order.deliveryTime().toLocalTime()))
                    .toList();
            Duration capacity = capacityLeft(schedule, ordersInSchedule);
            if (capacity.compareTo(maxCapacity) > 0) maxCapacity = capacity;
        }
        return maxCapacity;
    }

    private Duration capacityLeft(Schedule schedule, List<Order> orders) {
        Duration totalPreparationTimeOrders = orders.stream()
                .map(Order::preparationTime)
                .reduce(Duration.ZERO, Duration::plus);
        return schedule.totalCapacity().minus(totalPreparationTimeOrders);
    }

    private boolean canAddOrder(LocalDateTime deliveryTime, List<Order> orders) throws IOException {
        if (deliveryTime == null || orders.isEmpty()) return true;
        long averagePreparationTime = getAveragePreparationTime(orders).toMinutes();
        if (averagePreparationTime == 0) return true;
        long maxNbOfOrder = getMaxCapacityLeft(deliveryTime).toMinutes() / averagePreparationTime;
        long currentNbOfOrder = orders.stream()
                .filter(order -> order.status() == Status.INITIALISED)
                .count();
        return currentNbOfOrder < maxNbOfOrder;
    }

    private Duration getAveragePreparationTime(List<Order> orders) {
        List<Duration> lastOrderDurations = orders.reversed().stream()
                .filter(order -> order.status().compareTo(Status.PAID) >= 0 && order.deliveryTime() != null)
                .limit(RELEVANT_NUMBER_OF_ORDER_FOR_MEAN_CALCULATION)
                .map(Order::preparationTime)
                .toList();
        if (lastOrderDurations.isEmpty()) return Duration.ZERO;
        return lastOrderDurations.stream()
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(lastOrderDurations.size());
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
            return MenuItemServiceHelper.getMenuItemByRestaurantId(id).stream().anyMatch(menuItem -> maxCapacity.compareTo(menuItem.preparationTime()) >= 0);
        } catch (Exception e) {
            return false;
        }
    }

    private Duration capacityLeft(Schedule schedule, LocalDate deliveryDate) throws IOException {
        List<Order> ordersTakenAccountSchedule = OrderServiceHelper.getOrderPastStatus(id, Status.PAID, LocalDateTime.of(deliveryDate, schedule.start()), LocalDateTime.of(deliveryDate, schedule.end()));
        Duration totalPreparationTimeOrders = ordersTakenAccountSchedule.stream()
                .map(Order::preparationTime)
                .reduce(Duration.ZERO, Duration::plus);
        return schedule.totalCapacity().minus(totalPreparationTimeOrders);
    }

    private Duration getMaxCapacityLeft(LocalDateTime arrivalTime) throws IOException {
        LocalDateTime deliveryTime = arrivalTime.minus(DELIVERY_TIME_RESTAURANT);
        Set<Schedule> schedulesBefore2Hours = new HashSet<>(ScheduleServiceHelper.getScheduleForDeliveryTime(getId(), deliveryTime, MAX_PREPARATION_DURATION_BEFORE_DELIVERY));
        Duration maxCapacity = Duration.ZERO;
        for (Schedule schedule : schedulesBefore2Hours) {
            Duration capacity = capacityLeft(schedule, deliveryTime.toLocalDate());
            if (capacity.compareTo(maxCapacity) > 0) maxCapacity = capacity;
        }
        return maxCapacity;
    }

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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Restaurant) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name + " [" + typeOfFood + "]";
    }
}

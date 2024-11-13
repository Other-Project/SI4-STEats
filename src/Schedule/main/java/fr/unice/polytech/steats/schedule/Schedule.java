package fr.unice.polytech.steats.schedule;

import fr.unice.polytech.steats.order.Order;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * @author Team C
 */
public class Schedule implements Comparable<Schedule> {
    private final String scheduleId;
    private final DayOfWeek dayOfWeek;
    private final LocalTime start;
    private final Duration duration;
    private final int nbPerson;
    private final String restaurantId;

    /**
     * @param start     The Localtime at which the schedule start
     * @param duration  The duration of the schedule
     * @param nbPerson  The number of person that works during the schedule
     * @param dayOfWeek The day of the week of the schedule
     */
    public Schedule(String scheduleId, LocalTime start, Duration duration,
                    int nbPerson, DayOfWeek dayOfWeek, String restaurantId) {
        this.scheduleId = scheduleId;
        this.start = start;
        this.duration = duration;
        this.nbPerson = nbPerson;
        this.dayOfWeek = dayOfWeek;
        this.restaurantId = restaurantId;
    }

    /**
     * @return the schedule's id
     */
    public String getScheduleId() {
        return scheduleId;
    }

    /**
     * @return The start of the schedule
     */
    public LocalTime getStart() {
        return this.start;
    }

    /**
     * @return The schedule's duration
     */
    public Duration getDuration() {
        return this.duration;
    }

    public LocalTime getEnd() {
        return start.plus(duration);
    }

    /**
     * @return The schedule's capacity
     */
    public Duration getTotalCapacity() {
        return duration.multipliedBy(nbPerson);
    }

    /**
     * The day of the week of the schedule
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * The number of person that works during the schedule
     */
    public int getNbPerson() {
        return nbPerson;
    }

    /**
     * @return the associated restaurant's id
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * @return If the schedule corresponds to the delivery time given in args
     */
    public boolean contains(LocalDateTime deliveryTime) {
        return deliveryTime.getDayOfWeek() == dayOfWeek && start.isBefore(deliveryTime.toLocalTime()) && getEnd().isAfter(deliveryTime.toLocalTime());
    }

    /**
     * If the schedule contains the order
     *
     * @param order The order to check
     */
    public boolean contains(Order order) {
        LocalDateTime deliveryTime = order.getDeliveryTime();
        return deliveryTime.getDayOfWeek() == dayOfWeek && !start.isAfter(deliveryTime.toLocalTime()) && getEnd().isAfter(deliveryTime.toLocalTime());
    }

    /**
     * Check if the schedule is between two dates
     *
     * @param start The start of the period
     * @param end   The end of the period
     */
    public boolean isBetween(LocalDateTime start, LocalDateTime end) {
        if (compareTo(start) < 0) return false;
        if (compareTo(end) >= 0) return false;
        if (contains(end)) return false;
        return !contains(start);
    }

    /**
     * @param other The other schedule
     * @return If one of the schedules overlap with the other one
     */
    public boolean overlap(Schedule other) {
        return start.plus(duration).isAfter(other.start) && start.plus(duration).isBefore(other.start.plus(other.duration)) && other.start.plus(duration).isAfter(start) && other.start.plus(duration).isBefore(other.start.plus(duration));
    }

    @Override
    public int compareTo(Schedule o) {
        if (dayOfWeek.getValue() < o.dayOfWeek.getValue()) return -1;
        else if (dayOfWeek.getValue() > o.dayOfWeek.getValue()) return 1;
        else if (start.isBefore(o.start)) return -1;
        else if (start.isAfter(o.start)) return 1;
        return 0;
    }

    /**
     * Compare the schedule with a date
     *
     * @param dateTime The date to compare
     */
    @SuppressWarnings("java:S4351")
    public int compareTo(LocalDateTime dateTime) {
        int compareDayOfWeek = dayOfWeek.compareTo(dateTime.getDayOfWeek());
        if (compareDayOfWeek != 0)
            return ((Math.absExact(compareDayOfWeek) + 3) % 7 - 3) * (compareDayOfWeek > 0 ? 1 : -1);
        if (start.isAfter(dateTime.toLocalTime())) return 1;
        if (!getEnd().isAfter(dateTime.toLocalTime())) return -1;
        return 0;
    }

    @Override
    public String toString() {
        return start + " - " + getEnd() + " (" + dayOfWeek + ") [" + getTotalCapacity() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule schedule)) return false;
        return nbPerson == schedule.nbPerson && dayOfWeek == schedule.dayOfWeek && Objects.equals(start, schedule.start) && Objects.equals(duration, schedule.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, start, duration, nbPerson);
    }
}

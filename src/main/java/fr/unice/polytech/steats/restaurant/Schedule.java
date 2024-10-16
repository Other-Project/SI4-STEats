package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.order.Order;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Team C
 */
public class Schedule implements Comparable<Schedule> {
    private DayOfWeek dayOfWeek;
    private LocalTime start;
    private Duration duration;
    private int nbPerson;

    /**
     * @param start     The Localtime at which the schedule start
     * @param duration  The duration of the schedule
     * @param nbPerson  The number of person that works during the schedule
     * @param dayOfWeek The day of the week of the schedule
     */
    public Schedule(LocalTime start, Duration duration, int nbPerson, DayOfWeek dayOfWeek) {
        this.start = start;
        this.duration = duration;
        this.nbPerson = nbPerson;
        this.dayOfWeek = dayOfWeek;
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
     * @return If the schedule corresponds to the delivery time given in args
     */
    public Boolean contains(LocalDateTime deliveryTime) {
        return deliveryTime.getDayOfWeek() == dayOfWeek && start.isBefore(deliveryTime.toLocalTime()) && getEnd().isAfter(deliveryTime.toLocalTime());
    }

    public Boolean contains(Order order) {
        LocalDateTime deliveryTime = order.getDeliveryTime();
        return deliveryTime.getDayOfWeek() == dayOfWeek && !start.isAfter(deliveryTime.toLocalTime()) && getEnd().isAfter(deliveryTime.toLocalTime());
    }

    public boolean isBetween(LocalDateTime start, LocalDateTime end) {
        if (compareTo(start) >= 0) return false;
        if (compareTo(end) < 0) return false;
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

    public int compareTo(LocalDateTime dateTime) {
        int compareDayOfWeek = dayOfWeek.compareTo(dateTime.getDayOfWeek());
        if (compareDayOfWeek != 0) return compareDayOfWeek;
        if (start.isAfter(dateTime.toLocalTime())) return 1;
        if (!getEnd().isAfter(dateTime.toLocalTime())) return -1;
        return 0;
    }

    public String toString() {
        return start + " - " + getEnd() + " (" + dayOfWeek + ") [" + getTotalCapacity() + "]";
    }
}

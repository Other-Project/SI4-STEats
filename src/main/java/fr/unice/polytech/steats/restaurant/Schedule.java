package fr.unice.polytech.steats.restaurant;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;

/**
 * @author Team C
 */
public class Schedule {
    private DayOfWeek dayOfWeek;
    private LocalTime start;
    private Duration duration;
    private Duration totalCapacity;

    /**
     * @param start         The Localtime at which the schedule start
     * @param duration      The duration of the schedule
     * @param totalCapacity The capacity of the schedule
     * @param dayOfWeek     The day of the week of the schedule
     */
    public Schedule(LocalTime start, Duration duration, Duration totalCapacity, DayOfWeek dayOfWeek) {
        this.start = start;
        this.duration = duration;
        this.totalCapacity = totalCapacity;
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

    /**
     * @return The schedule's capacity
     */
    public Duration getTotalCapacity() {
        return this.totalCapacity;
    }

    /**
     * @return If the schedule corresponds to the delivery time given in args
     */
    public Boolean contains(LocalDateTime deliveryTime) {
        return deliveryTime.getDayOfWeek() == dayOfWeek && deliveryTime.isAfter(ChronoLocalDateTime.from(start)) && Duration.between(start, deliveryTime).compareTo(duration.multipliedBy(2)) < 0 && Duration.between(start.plus(duration), deliveryTime).compareTo(duration) > 0;
    }

    /**
     * @param other The other schedule
     * @return If one of the schedules overlap with the other one
     */
    public boolean overlap(Schedule other) {
        return start.plus(duration).isAfter(other.start) && start.plus(duration).isBefore(other.start.plus(other.duration)) && other.start.plus(duration).isAfter(start) && other.start.plus(duration).isBefore(other.start.plus(duration));
    }
}

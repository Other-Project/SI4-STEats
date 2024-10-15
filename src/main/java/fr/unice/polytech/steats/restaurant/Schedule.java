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
    private Duration scheduleDuration;
    private Duration totalCapacity;

    /**
     * @param start            The Localtime at which
     * @param scheduleDuration
     * @param capacity
     * @param dayOfWeek
     */
    public Schedule(LocalTime start, Duration scheduleDuration, Duration capacity, DayOfWeek dayOfWeek) {
        this.start = start;
        this.scheduleDuration = scheduleDuration;
        this.totalCapacity = capacity;
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStart() {
        return this.start;
    }

    public Duration getScheduleDuration() {
        return this.scheduleDuration;
    }

    public Duration getTotalCapacity() {
        return this.totalCapacity;
    }

    public Boolean contains(LocalDateTime deliveryTime) {
        return deliveryTime.getDayOfWeek() == dayOfWeek && deliveryTime.isAfter(ChronoLocalDateTime.from(start)) && Duration.between(start, deliveryTime).compareTo(scheduleDuration.multipliedBy(2)) < 0 && Duration.between(start.plus(scheduleDuration), deliveryTime).compareTo(scheduleDuration) > 0;
    }

    public boolean overlap(Schedule other) {
        return start.plus(scheduleDuration).isAfter(other.start) && start.plus(scheduleDuration).isBefore(other.start.plus(other.scheduleDuration)) && other.start.plus(scheduleDuration).isAfter(start) && other.start.plus(scheduleDuration).isBefore(other.start.plus(scheduleDuration));
    }
}

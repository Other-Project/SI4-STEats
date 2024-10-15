package fr.unice.polytech.steats.restaurant;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Team C
 */
public class Schedule {
    private DayOfWeek dayOfWeek;
    private LocalTime start;
    private Duration scheduleDuration;
    private Duration totalCapacity;

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
        return deliveryTime.getDayOfWeek() == dayOfWeek && Duration.between(start, deliveryTime).dividedBy(2).compareTo(scheduleDuration) < 0;
    }
}

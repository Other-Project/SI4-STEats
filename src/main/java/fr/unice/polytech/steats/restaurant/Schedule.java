package fr.unice.polytech.steats.restaurant;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Schedule {
    private DayOfWeek dayOfWeek;
    private LocalTime start;
    private LocalTime end;
    private Duration capacity;

    public Schedule(LocalTime start, LocalTime end, Duration capacity, DayOfWeek dayOfWeek) {
        this.start = start;
        this.end = end;
        this.capacity = capacity;
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStart() {
        return this.start;
    }

    public LocalTime getEnd() {
        return this.end;
    }

    public Duration getCapacity() {
        return this.capacity;
    }

    public Boolean contains(LocalDateTime deliveryTime) {
        return deliveryTime.getDayOfWeek() == dayOfWeek && ChronoUnit.MINUTES.between(deliveryTime, start) < ChronoUnit.MINUTES.between(end, start);
    }
}

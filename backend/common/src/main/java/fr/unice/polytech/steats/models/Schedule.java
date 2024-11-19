package fr.unice.polytech.steats.models;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public record Schedule(String id, LocalTime start, Duration duration, int nbPerson, DayOfWeek dayOfWeek,
                       String restaurantId, LocalTime end, Duration totalCapacity) {
}

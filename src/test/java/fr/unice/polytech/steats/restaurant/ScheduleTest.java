package fr.unice.polytech.steats.restaurant;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduleTest {

    private static final List<Schedule> scheduleList = new ArrayList<>();

    @BeforeAll
    public static void setUpSchedules() {
        for (int i = 0; i < 8; i++) {
            scheduleList.add(new Schedule(
                    LocalTime.of(19, 0).plus(Duration.ofMinutes(30).multipliedBy(i)),
                    Duration.ofMinutes(30),
                    1,
                    DayOfWeek.FRIDAY
            ));
        }
    }

    @Test
    public void test_schedules_edge_case() {
        LocalDateTime deliveryTime = LocalDateTime.of(2024, 10, 18, 21, 30);
        assertEquals(deliveryTime.getDayOfWeek(), DayOfWeek.FRIDAY);
        List<Schedule> schedulesSorted = scheduleList
                .stream()
                .filter(schedule -> schedule.isBetween(deliveryTime, deliveryTime.minus(Duration.ofHours(2))))
                .toList();
        assertEquals(4, schedulesSorted.size());
        assertTrue(schedulesSorted.stream()
                .noneMatch(schedule -> schedule.getStart().isBefore(LocalTime.of(19, 30))
                        || schedule.getEnd().isAfter(LocalTime.of(21, 30))));
        ;
    }

    @Test
    public void test_schedules_general_case() {
        LocalDateTime deliveryTime = LocalDateTime.of(2024, 10, 18, 21, 45);
        assertEquals(deliveryTime.getDayOfWeek(), DayOfWeek.FRIDAY);
        List<Schedule> schedulesSorted = scheduleList
                .stream()
                .filter(schedule -> schedule.isBetween(deliveryTime, deliveryTime.minus(Duration.ofHours(2))))
                .toList();
        assertEquals(3, schedulesSorted.size());
        assertTrue(schedulesSorted.stream()
                .noneMatch(schedule -> schedule.getStart().isBefore(LocalTime.of(20, 0))
                        || schedule.getEnd().isAfter(LocalTime.of(21, 30))));
    }
}

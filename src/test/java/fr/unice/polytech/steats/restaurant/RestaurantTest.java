package fr.unice.polytech.steats.restaurant;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestaurantTest {

    private void addScheduleForPeriod(Restaurant restaurant, int nbPersons, DayOfWeek startDay, LocalTime startTime, DayOfWeek endDay, LocalTime endTime) {
        DayOfWeek day = startDay;
        for (LocalTime time = startTime; day != endDay || time.isBefore(endTime); time = time.plus(restaurant.getScheduleDuration())) {
            restaurant.addSchedule(new Schedule(time, restaurant.getScheduleDuration(), nbPersons, day));
            if (time.equals(LocalTime.of(0, 0).minus(restaurant.getScheduleDuration())))
                day = day.plus(1);
        }
    }

    @Test
    void getOpeningTimes() {
        Restaurant restaurant = new Restaurant("");
        addScheduleForPeriod(restaurant, 5, DayOfWeek.MONDAY, LocalTime.of(10, 0), DayOfWeek.MONDAY, LocalTime.of(14, 30));
        addScheduleForPeriod(restaurant, 3, DayOfWeek.MONDAY, LocalTime.of(16, 0), DayOfWeek.MONDAY, LocalTime.of(17, 30));
        addScheduleForPeriod(restaurant, 5, DayOfWeek.MONDAY, LocalTime.of(17, 30), DayOfWeek.MONDAY, LocalTime.of(19, 0));
        addScheduleForPeriod(restaurant, 10, DayOfWeek.MONDAY, LocalTime.of(19, 0), DayOfWeek.TUESDAY, LocalTime.of(1, 0));
        addScheduleForPeriod(restaurant, 10, DayOfWeek.FRIDAY, LocalTime.of(18, 0), DayOfWeek.MONDAY, LocalTime.of(3, 0));

        Map<DayOfWeek, List<OpeningTime>> openingByDays = Map.of(
                DayOfWeek.MONDAY, List.of(
                        new OpeningTime(LocalTime.of(0, 0), LocalTime.of(3, 0)),
                        new OpeningTime(LocalTime.of(10, 0), LocalTime.of(14, 30)),
                        new OpeningTime(LocalTime.of(16, 0), LocalTime.of(23, 59, 59))
                ),
                DayOfWeek.TUESDAY, List.of(new OpeningTime(LocalTime.of(0, 0), LocalTime.of(1, 0))),
                DayOfWeek.FRIDAY, List.of(new OpeningTime(LocalTime.of(18, 0), LocalTime.of(23, 59, 59))),
                DayOfWeek.SATURDAY, List.of(new OpeningTime(LocalTime.of(0, 0), LocalTime.of(23, 59, 59))),
                DayOfWeek.SUNDAY, List.of(new OpeningTime(LocalTime.of(0, 0), LocalTime.of(23, 59, 59)))
        );
        openingByDays.forEach((day, intervals) -> assertEquals(intervals, restaurant.getOpeningTimes(day)));
    }
}
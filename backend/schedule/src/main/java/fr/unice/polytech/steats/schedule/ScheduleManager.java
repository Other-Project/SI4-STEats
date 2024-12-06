package fr.unice.polytech.steats.schedule;

import fr.unice.polytech.steats.helpers.RestaurantServiceHelper;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SuppressWarnings("java:S6548")
public class ScheduleManager extends AbstractManager<Schedule> {
    private static final ScheduleManager INSTANCE = new ScheduleManager();

    private ScheduleManager() {
        super();
    }

    /**
     * Get the instance of the ScheduleManager
     *
     * @return The instance of the ScheduleManager
     */
    public static ScheduleManager getInstance() {
        return INSTANCE;
    }


    @Override
    public void add(Schedule schedule) {
        Duration restaurantScheduleDuration;
        try {
            restaurantScheduleDuration = RestaurantServiceHelper.getRestaurant(schedule.getRestaurantId()).scheduleDuration();
        } catch (IOException e) {
            throw new IllegalStateException("This schedule's restaurant does not exist (schedule's restaurantId : " + schedule.getRestaurantId() + ")");
        }
        if (!schedule.getDuration().equals(restaurantScheduleDuration))
            throw new IllegalArgumentException("This schedule's duration does not coincide with the restaurant' schedule duration");
        if (getAll().stream().anyMatch(s -> s.overlap(schedule)))
            throw new IllegalArgumentException("This schedule overlaps with another schedule of the restaurant");
        super.add(schedule.getId(), schedule);
    }

    public List<Schedule> getSchedule(String restaurantId, LocalDateTime startTime, LocalDateTime endTime) {
        Stream<Schedule> schedules = getAll().stream();
        if (restaurantId != null)
            schedules = schedules.filter(schedule -> restaurantId.equals(schedule.getRestaurantId()));
        if (startTime != null)
            schedules = schedules.filter(schedule -> schedule.compareTo(startTime) >= 0 && !schedule.contains(startTime));
        if (endTime != null)
            schedules = schedules.filter(schedule -> schedule.compareTo(endTime) < 0 && !schedule.contains(endTime));
        return schedules.toList();
    }

    public List<Schedule> getScheduleByRestaurantId(String restaurantId) {
        return getAll().stream().filter(schedule -> schedule.getRestaurantId().equals(restaurantId)).toList();
    }

    /**
     * Add schedules for a period of time
     *
     * @param nbPersons    The number of working persons for the schedule
     * @param startDay     The day of the week to start the period
     * @param startTime    The time to start the period
     * @param endDay       The day of the week to end the period
     * @param endTime      The time to end the period
     * @param restaurantId The restaurant id of the schedule
     * @param duration     The duration of the schedule
     */
    public void addScheduleForPeriod(int nbPersons, DayOfWeek startDay, LocalTime startTime, DayOfWeek endDay, LocalTime endTime, String restaurantId, Duration duration) {
        DayOfWeek day = startDay;
        long seconds = Math.ceilDiv(startTime.toSecondOfDay(), duration.toSeconds()) * duration.toSeconds();  // round the start time to the nearest schedule
        if (seconds >= 86400) {
            seconds = 0;
            day = day.plus(1);
        }
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        for (; day != endDay || (!time.plus(duration).isAfter(endTime) && !time.plus(duration).equals(LocalTime.MIN)); time = time.plus(duration)) {
            String id = UUID.randomUUID().toString();
            super.add(id, new Schedule(id, time, duration, nbPersons, day, restaurantId));
            if (time.equals(LocalTime.of(0, 0).minus(duration)))
                day = day.plus(1);
        }
    }

    public void demo() {

        // Schedule for restaurant 1 open every day from 8:00 to 22:00
        for (DayOfWeek day : DayOfWeek.values()) {
            addScheduleForPeriod(3, day, LocalTime.of(8, 0), day, LocalTime.of(22, 0), "1", Duration.ofMinutes(30));
        }

        // Schedule for restaurant 2 open every day from 11:00-14:00 and 18:00-23:00
        for (DayOfWeek day : DayOfWeek.values()) {
            addScheduleForPeriod(2, day, LocalTime.of(11, 0), day, LocalTime.of(14, 0), "2", Duration.ofMinutes(30));
            addScheduleForPeriod(2, day, LocalTime.of(18, 0), day, LocalTime.of(23, 0), "2", Duration.ofMinutes(30));
        }

        // Schedule for restaurant 3 open weekends from 19:00 to 3:00
        addScheduleForPeriod(1, DayOfWeek.SATURDAY, LocalTime.of(19, 0), DayOfWeek.SUNDAY, LocalTime.of(3, 0), "3", Duration.ofMinutes(30));
        addScheduleForPeriod(1, DayOfWeek.SUNDAY, LocalTime.of(19, 0), DayOfWeek.MONDAY, LocalTime.of(3, 0), "3", Duration.ofMinutes(30));
    }
}

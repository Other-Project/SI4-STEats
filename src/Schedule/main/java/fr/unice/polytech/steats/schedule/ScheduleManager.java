package fr.unice.polytech.steats.schedule;

import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.utils.AbstractManager;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

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
            restaurantScheduleDuration = RestaurantManager.getScheduleDurationForRestaurant(schedule.getRestaurantId());
        } catch (NotFoundException e) {
            throw new IllegalStateException("This schedule's restaurant does not exist (schedule's restaurantId : " + schedule.getRestaurantId() + ")");
        }
        if (!schedule.getDuration().equals(restaurantScheduleDuration))
            throw new IllegalArgumentException("This schedule's duration does not coincide with the restaurant' schedule duration");
        if (getAll().stream().anyMatch(s -> s.overlap(schedule)))
            throw new IllegalArgumentException("This schedule overlaps with another schedule of the restaurant");
        super.add(schedule.getId(), schedule);
    }

    public List<Schedule> getScheduleByRestaurantId(String restaurantId) {
        return getAll().stream().filter(schedule -> schedule.getRestaurantId().equals(restaurantId)).toList();
    }

    public void demo() {
        Schedule schedule = new Schedule("1", LocalTime.of(10, 0), Duration.ofMinutes(30), 5, DayOfWeek.FRIDAY, "1");
        add(schedule.getId(), schedule);
        Schedule schedule2 = new Schedule("2", LocalTime.of(10, 30), Duration.ofMinutes(30), 5, DayOfWeek.FRIDAY, "1");
        add(schedule2.getId(), schedule2);
        Schedule schedule3 = new Schedule("3", LocalTime.of(11, 0), Duration.ofMinutes(30), 5, DayOfWeek.FRIDAY, "1");
        add(schedule3.getId(), schedule3);
        Schedule schedule4 = new Schedule("4", LocalTime.of(11, 30), Duration.ofMinutes(30), 5, DayOfWeek.FRIDAY, "1");
        add(schedule4.getId(), schedule4);
        Schedule schedule5 = new Schedule("5", LocalTime.of(12, 0), Duration.ofMinutes(30), 5, DayOfWeek.MONDAY, "1");
        add(schedule5.getId(), schedule5);
        Schedule schedule6 = new Schedule("6", LocalTime.of(12, 30), Duration.ofMinutes(30), 5, DayOfWeek.MONDAY, "2");
        add(schedule6.getId(), schedule6);
    }
}

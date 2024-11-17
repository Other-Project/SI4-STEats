package fr.unice.polytech.steats.schedule;

import fr.unice.polytech.steats.utils.AbstractManager;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

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
    public void add(Schedule item) {
        super.add(item.getId(), item);
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
    }
}

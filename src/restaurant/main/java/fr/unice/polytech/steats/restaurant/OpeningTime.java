package fr.unice.polytech.steats.restaurant;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents an opening time of a {&link Restaurant}
 *
 * @author Team C
 */
public class OpeningTime {
    private final LocalTime start;
    private LocalTime end;

    OpeningTime(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    /**
     * The start of the opening time
     */
    public LocalTime getStart() {
        return start;
    }

    /**
     * The end of the opening time
     */
    public LocalTime getEnd() {
        return end;
    }

    /**
     * Set the end of the opening time
     */
    void setEnd(LocalTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return start + " - " + end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof OpeningTime that)) return false;
        return Objects.equals(this.start, that.start) && Objects.equals(this.end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}

package fr.unice.polytech.steats.restaurant;

import java.time.LocalTime;

public class Schedule {
    private LocalTime start;
    private LocalTime end;
    private int nbPerson;

    public Schedule(LocalTime start, LocalTime end, int nbPerson) {
        this.start = start;
        this.end = end;
        this.nbPerson = nbPerson;
    }

    public LocalTime getStart() {
        return this.start;
    }

    public LocalTime getEnd() {
        return this.end;
    }

    public int getNbPerson() {
        return this.nbPerson;
    }
}

package fr.unice.polytech.steats.restaurant;

import java.sql.Time;

public class Schedule {
    private Time start;
    private Time end;
    private int nbPerson;

    public Schedule(Time start, Time end, int nbPerson) {
        this.start = start;
        this.end = end;
        this.nbPerson = nbPerson;
    }

    public Time getStart() {
        return this.start;
    }

    public Time getEnd() {
        return this.end;
    }

    public int getNbPerson() {
        return this.nbPerson;
    }
}

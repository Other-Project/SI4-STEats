package fr.unice.polytech.steats.restaurant;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public record Restaurant(String name, List<MenuItem> menu, TypeOfFood typeOfFood) {

    public Restaurant(String name) {
        this(name, new ArrayList<>(), TypeOfFood.CLASSIC);
    }

    public Restaurant(String name, List<MenuItem> menu, TypeOfFood typeOfFood) {
        this.name = name;
        this.menu = menu;
        this.typeOfFood = typeOfFood;
    }

    public List<MenuItem> getAvailableMenu(LocalTime deliveryTime) {
        return this.menu;
    }
}

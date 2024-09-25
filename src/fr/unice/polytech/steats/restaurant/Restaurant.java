package fr.unice.polytech.steats.restaurant;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private final String name;
    private List<MenuItem> menu;
    private final List<TypeOfFood> typeOfFood;

    public Restaurant(String name) {
        this(name, List.of(TypeOfFood.SNACKS, TypeOfFood.DESERTS, TypeOfFood.DRINKS));
    }

    public Restaurant(String name, List<TypeOfFood> typeOfFood) {
        this.name = name;
        this.menu = new ArrayList<>();
        this.typeOfFood = typeOfFood;
    }

    public String getName() {
        return this.name;
    }

    public List<MenuItem> getMenu() {
        return this.menu;
    }

    public List<MenuItem> getMenu(LocalTime deliveryTime) {
        return this.menu;
    }

    public List<TypeOfFood> getTypeOfFood() {
        return this.typeOfFood;
    }
}

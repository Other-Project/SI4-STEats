package fr.unice.polytech.steats.restaurant;

import java.time.LocalDateTime;
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

    // TODO : filter the menu according to deliveryTime
    public List<MenuItem> getAvailableMenu(LocalDateTime deliveryTime) {
        return new ArrayList<>(this.menu);
    }

    public List<MenuItem> getMenu() {
        return new ArrayList<>(this.menu);
    }

    public void addMenuItem(MenuItem menuItem) {
        this.menu.add(menuItem);
    }

    public void removeMenuItem(MenuItem menuItem) {

    }

    public List<MenuItem> getFullMenu() {
        return this.menu;
    }
}

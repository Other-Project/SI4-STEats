package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.order.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    private String name;
    private List<MenuItem> menu;
    private TypeOfFood typeOfFood;
    private List<Order> orders;

    public Restaurant(String name) {
        this(name, new ArrayList<>(), TypeOfFood.CLASSIC);
    }

    public Restaurant(String name, List<MenuItem> menu, TypeOfFood typeOfFood) {
        this.name = name;
        this.menu = menu;
        this.typeOfFood = typeOfFood;
        this.orders = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public TypeOfFood getTypeOfFood() {
        return typeOfFood;
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
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
        this.menu.remove(menuItem);
    }

    public List<MenuItem> getFullMenu() {
        return this.menu;
    }

    /**
     * Add an order for the restaurant
     *
     * @param order the order to add
     */
    public void addOrder(Order order) {
        this.orders.add(order);
    }
}

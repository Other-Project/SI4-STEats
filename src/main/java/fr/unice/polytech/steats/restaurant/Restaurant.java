package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.user.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * A restaurant that serves food
 *
 * @author Team C
 */
public class Restaurant {
    private final String name;
    private final List<MenuItem> menu;
    private final TypeOfFood typeOfFood;
    private final List<Discount> discounts;
    private final List<Order> orders;

    public Restaurant(String name, TypeOfFood typeOfFood, List<MenuItem> menu, List<Discount> discounts) {
        this.name = name;
        this.menu = menu;
        this.typeOfFood = typeOfFood;
        this.discounts = discounts;
        this.orders = new ArrayList<>();
    }

    public Restaurant(String name) {
        this(name, TypeOfFood.CLASSIC);
    }

    public Restaurant(String name, TypeOfFood typeOfFood) {
        this(name, typeOfFood, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Name of the restaurant
     */
    public String getName() {
        return name;
    }

    /**
     * The full menu of the restaurant
     */
    public List<MenuItem> getFullMenu() {
        return Collections.unmodifiableList(menu);
    }

    /**
     * The type of food the restaurant serves
     */
    public TypeOfFood getTypeOfFood() {
        return typeOfFood;
    }

    /**
     * All the discounts the restaurant proposes
     */
    public List<Discount> discounts() {
        return Collections.unmodifiableList(discounts);
    }

    /**
     * All the orders received by the restaurant
     */
    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * The discounts that can be applied to an order
     *
     * @param order The order to check
     */
    public List<Discount> availableDiscounts(SingleOrder order) {
        List<Discount> applicableDiscounts = discounts().stream().filter(discount -> {
            try {
                return discount.isApplicable(order);
            } catch (NotFoundException e) {
                return false;
            }
        }).toList();
        List<Discount> res = new ArrayList<>(applicableDiscounts.stream().filter(Discount::isStackable).toList());
        applicableDiscounts.stream()
                .filter(discount -> !discount.isStackable())
                .max(Comparator.comparingDouble(discount -> discount.value(order.getSubPrice())))
                .ifPresent(res::add);
        return res;
    }

    /**
     * The part of the menu that can be prepared and delivered in time
     *
     * @param deliveryTime Wanted time of delivery
     */
    public List<MenuItem> getAvailableMenu(LocalDateTime deliveryTime) {
        // TODO : filter the menu according to deliveryTime
        return new ArrayList<>(this.menu);
    }

    /**
     * Add a menu item to the restaurant
     *
     * @param menuItem The menu item
     */
    public void addMenuItem(MenuItem menuItem) {
        this.menu.add(menuItem);
    }

    /**
     * Remove a menu item to the restaurant
     *
     * @param menuItem The menu item
     */
    public void removeMenuItem(MenuItem menuItem) {
        this.menu.remove(menuItem);
    }

    /**
     * Add a discount to the restaurant
     *
     * @param discount The discount
     */
    public void addDiscount(Discount discount) {
        this.discounts.add(discount);
    }

    /**
     * Remove a discount of the restaurant
     *
     * @param discount The discount
     */
    public void removeDiscount(Discount discount) {
        this.discounts.remove(discount);
    }

    /**
     * Add an order for the restaurant
     *
     * @param order the order to add
     */
    public void addOrder(Order order) {
        this.orders.add(order);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Restaurant) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.menu, that.menu) &&
                Objects.equals(this.typeOfFood, that.typeOfFood) &&
                Objects.equals(this.discounts, that.discounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, menu, typeOfFood, discounts);
    }

    @Override
    public String toString() {
        return name + " [" + typeOfFood + "]";
    }
}

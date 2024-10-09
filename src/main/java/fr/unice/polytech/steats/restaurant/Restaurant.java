package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.order.SingleOrder;

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

    public Restaurant(String name, List<MenuItem> menu, TypeOfFood typeOfFood, List<Discount> discounts) {
        this.name = name;
        this.menu = menu;
        this.typeOfFood = typeOfFood;
        this.discounts = discounts;
    }

    public Restaurant(String name) {
        this(name, new ArrayList<>(), TypeOfFood.CLASSIC, new ArrayList<>());
    }

    /**
     * Name of the restaurant
     */
    public String name() {
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
    public TypeOfFood typeOfFood() {
        return typeOfFood;
    }

    /**
     * All the discounts the restaurant proposes
     */
    public List<Discount> discounts() {
        return Collections.unmodifiableList(discounts);
    }

    /**
     * The discounts that can be applied to an order
     *
     * @param order The order to check
     */
    public List<Discount> availableDiscounts(SingleOrder order) {
        List<Discount> applicableDiscounts = discounts().stream().filter(discount -> discount.isApplicable(order)).toList();
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
        return this.menu;
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

package fr.unice.polytech.steats.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"value"})
public record RestaurantDiscount(String id, String restaurantId, Options options, Criteria criteria, Effects effects) {
    /**
     * Gets the price of the order once the discount has been applied
     *
     * @param price The price of the order
     */
    public double getNewPrice(double price) {
        return (price - effects.orderCredit()) * (1 - effects.orderDiscount());
    }
}

package fr.unice.polytech.steats.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonSerialize(as = IOrder.class)
public interface IOrder {

    @JsonProperty("groupCode")
    String groupCode();

    @JsonProperty("deliveryTime")
    LocalDateTime deliveryTime();

    @JsonProperty("addressId")
    String addressId();

    @JsonProperty("restaurantId")
    String restaurantId();

    @JsonProperty("status")
    Status status();

    @JsonProperty("items")
    Map<String, Integer> items();

    @JsonProperty("discounts")
    List<String> discounts();

    @JsonProperty("preparationTime")
    Duration preparationTime();

    @JsonProperty("orderTime")
    LocalDateTime orderTime();

    @JsonProperty("price")
    double price();
}

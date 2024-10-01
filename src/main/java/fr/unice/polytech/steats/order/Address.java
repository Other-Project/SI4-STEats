package fr.unice.polytech.steats.order;

/**
 * Represents the address where an order can be delivered
 *
 * @param street             The street where the user wants the order to be delivered
 * @param city               The city where the order must be delivered
 * @param postal_code        The postal code of the city
 * @param additional_address Additional information about the address
 * @author Team C
 */
public record Address(String street, String city, String postal_code, String additional_address) {

}

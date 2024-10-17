package fr.unice.polytech.steats;

/**
 * Utility class for communication with the payment system.
 *
 * @author Team C
 */
public class PaymentSystem {

    private PaymentSystem() {
    }

    /**
     * Pay the order
     *
     * @param amount the amount to pay
     * @return if the payment was successful
     */
    public static boolean pay(double amount) {
        // call the external payment system
        return true;
    }
}

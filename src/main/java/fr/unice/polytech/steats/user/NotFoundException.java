package fr.unice.polytech.steats.user;

/**
 * UserNotFoundException is a custom exception that is thrown when a user is not found in the user registry
 * @author Team C
 */

public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}

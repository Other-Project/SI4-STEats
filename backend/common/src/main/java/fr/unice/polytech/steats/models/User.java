package fr.unice.polytech.steats.models;

/**
 * Represents the user
 *
 * @param name   The name of the user
 * @param userId The id of the user
 * @param role   The role of the user
 */
public record User(String name, String userId, Role role) {
}

package fr.unice.polytech.steats.models;

import java.time.LocalDateTime;

public record Options(boolean stackable, boolean appliesAfterOrder, LocalDateTime expirationDate) {
    /**
     * Is the discount expired
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }
}

package fr.unice.polytech.steats.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"expired"})
public record Options(boolean stackable, boolean appliesAfterOrder, LocalDateTime expirationDate) {
    /**
     * Is the discount expired
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }
}

package fr.unice.polytech.steats.discounts.applied;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a discount that has been applied to a user order.
 *
 * @param discountId     The id of the discount
 * @param userId         The id of the user
 * @param unlockOrderId  The id of the order that unlocked the discount
 * @param appliedOrderId The id of the order to which the discount has been applied
 */
public record AppliedDiscount(String discountId, String userId, String unlockOrderId, String appliedOrderId) {
    @JsonProperty("id")
    public String id() {
        return String.valueOf(hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppliedDiscount that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(discountId, that.discountId) && Objects.equals(unlockOrderId, that.unlockOrderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discountId, userId, unlockOrderId);
    }
}

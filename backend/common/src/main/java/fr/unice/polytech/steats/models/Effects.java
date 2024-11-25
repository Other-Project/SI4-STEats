package fr.unice.polytech.steats.models;

import java.util.Arrays;
import java.util.Objects;

public record Effects(double orderDiscount, double orderCredit, String... freeItemIds) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Effects(double orderDiscountOther, double orderCreditOther, String[] freeItemIdsOther))) return false;
        return Double.compare(orderCredit, orderCreditOther) == 0 && Double.compare(orderDiscount, orderDiscountOther) == 0 && Objects.deepEquals(freeItemIds, freeItemIdsOther);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderDiscount, orderCredit, Arrays.hashCode(freeItemIds));
    }

    @Override
    public String toString() {
        return "Effects{" +
                "orderDiscount=" + orderDiscount +
                ", orderCredit=" + orderCredit +
                ", freeItemIds=" + Arrays.toString(freeItemIds) +
                '}';
    }
}

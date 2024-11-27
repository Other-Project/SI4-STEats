package fr.unice.polytech.steats.models;

import java.util.Arrays;
import java.util.Objects;

public record Criteria(int ordersAmount, int currentOrderItemsAmount, int itemsAmount, Role... clientRole) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Criteria(int ordersAmountOther, int currentOrderItemsAmountOther, int itemsAmountOther, Role[] clientRoleOther))) return false;
        return itemsAmount == itemsAmountOther
                && ordersAmount == ordersAmountOther
                && currentOrderItemsAmount == currentOrderItemsAmountOther
                && Objects.deepEquals(clientRole, clientRoleOther);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordersAmount, currentOrderItemsAmount, itemsAmount, Arrays.hashCode(Arrays.stream(clientRole).map(Role::name).toArray()));
    }

    @Override
    public String toString() {
        return "Criteria{" +
                "ordersAmount=" + ordersAmount +
                ", currentOrderItemsAmount=" + currentOrderItemsAmount +
                ", itemsAmount=" + itemsAmount +
                ", clientRole=" + Arrays.toString(clientRole) +
                '}';
    }
}

package fr.unice.polytech.steats.order;

import java.util.HashMap;
import java.util.Map;

/**
 * Will manage the group orders
 * It will be able to create, delete, update, get and store group orders
 *
 * @author Team C
 */
public class GroupOrderManager {
    private static final Map<String, GroupOrder> groupOrders = new HashMap<>();
    private static final String GROUP_ORDER_DOES_NOT_EXIST = "The group order does not exist.";

    /**
     * Add a group order to the manager.
     *
     * @param groupOrder The group order to add
     */
    public void addGroupOrder(GroupOrder groupOrder) throws IllegalArgumentException {
        if (groupOrders.containsKey(groupOrder.getGroupCode()))
            throw new IllegalArgumentException("A group order with the same group code already exists.");
        groupOrders.put(groupOrder.getGroupCode(), groupOrder);
    }

    /**
     * Remove a group order from the manager.
     *
     * @param groupCode The group code of the group order to remove
     */
    public void removeGroupOrder(String groupCode) {
        if (!groupOrders.containsKey(groupCode)) throw new IllegalArgumentException(GROUP_ORDER_DOES_NOT_EXIST);
        groupOrders.remove(groupCode);
    }

    /**
     * Get a group order from the manager.
     *
     * @param groupCode The group code of the group order to get
     * @return The group order
     */
    public static GroupOrder getGroupOrder(String groupCode) {
        if (!groupOrders.containsKey(groupCode)) throw new IllegalArgumentException(GROUP_ORDER_DOES_NOT_EXIST);
        return groupOrders.get(groupCode);
    }

    /**
     * Get all the group orders from the manager.
     *
     * @return The group orders
     */
    public Map<String, GroupOrder> getGroupOrders() {
        return new HashMap<>(groupOrders);
    }
}


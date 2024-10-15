package fr.unice.polytech.steats.order;

import java.util.HashMap;
import java.util.Map;

/**
 * Will manage generic items
 * It will be able to create, delete, update, get and store items
 *
 * @author Team C
 */
public abstract class AbstractManager<T> {
    protected Map<String, T> items = new HashMap<>();

    /**
     * Add an Item to the manager.
     *
     * @param key  The key/id of the Item to add
     * @param item The Item to add
     */
    public void add(String key, T item) {
        if (items.containsKey(key)) throw new IllegalArgumentException("An item with the same key already exists.");
        items.put(key, item);
    }

    /**
     * Remove an Item from the manager.
     *
     * @param key The key/id of the Item to remove
     */
    public void remove(String key) {
        if (!items.containsKey(key)) throw new IllegalArgumentException("The item does not exist.");
        items.remove(key);
    }

    /**
     * Get an Item from the manager.
     *
     * @param key The key/id of the Item to get
     * @return The group order
     */
    public T get(String key) {
        if (!items.containsKey(key)) throw new IllegalArgumentException("The item does not exist.");
        return items.get(key);
    }

}
package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.user.NotFoundException;

import java.util.HashMap;
import java.util.List;
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
    public void remove(String key) throws NotFoundException {
        if (!items.containsKey(key)) throw new NotFoundException("The item does not exist.");
        items.remove(key);
    }

    /**
     * Get an Item from the manager.
     *
     * @param key The key/id of the Item to get
     * @return The group order
     */
    public T get(String key) throws NotFoundException {
        if (!items.containsKey(key)) throw new NotFoundException("The item does not exist.");
        return items.get(key);
    }

    /**
     * Get all the items of the manager
     *
     * @return A list of all the items in items
     */
    public List<T> getAll() {
        return items.values().stream().toList();
    }

    /**
     * Check if the manager contains an Item.
     *
     * @param key The key/id of the Item to check
     */
    public boolean contains(String key) {
        return items.containsKey(key);
    }

    /**
     * Clear the manager.
     */
    public void clear() {
        items.clear();
    }
}
package fr.unice.polytech.steats;

import fr.unice.polytech.steats.user.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage generic items
 * It stores items and is able to create, delete and get them
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
     * @return The value associated with the key
     */
    public T get(String key) throws NotFoundException {
        if (!items.containsKey(key)) throw new NotFoundException("The item does not exist.");
        return items.get(key);
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

    /**
     * Get all the items in the manager.
     *
     * @return The list of items in the manager
     */
    public List<T> getAll() {
        return new ArrayList<>(items.values());
    }
}
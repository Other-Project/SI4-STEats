package fr.unice.polytech.steats.restaurant;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenuItemTest {

    @Test
    public void testMenuItemNameHashed() {
        MenuItem menuItem = new MenuItem("Big Mac", 5.0, null);
        MenuItem menuItem2 = new MenuItem("Big Mac", 5.0, null);
        assertEquals(menuItem, menuItem2);
        assertEquals(menuItem.hashCode(), menuItem2.hashCode());
    }

    @Test
    public void testMenuItemToString() {
        MenuItem menuItem = new MenuItem("Big Mac", 5.0, Duration.ofMinutes(15));
        assertEquals("Big Mac (5.0€) [PT15M]", menuItem.toString());
    }
}

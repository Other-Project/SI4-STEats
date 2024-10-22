package fr.unice.polytech.steats.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void testUser() {
        User user = new User("John", "Doe", null);
        user.setName("Georges");
        assertEquals("Georges", user.getName());
    }
}

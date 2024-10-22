package fr.unice.polytech.steats.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    public void testUser() {
        User user = new User("John", "Doe", null);
        user.setName("Georges");
        assertEquals(user.getName(), "Georges");
    }
}

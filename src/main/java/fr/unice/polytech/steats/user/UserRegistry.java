package fr.unice.polytech.steats.user;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is used to manage the users of the university.
 * We choose to return optional and not to deal with exceptions as we did in the library.
 * We could have used exceptions, but we wanted to show that we can use optional.
 *
 * @author Team c
 **/

public class UserRegistry {

    Map<String, User> users = new HashMap<>();

    public UserRegistry() {
        User User1 = new User("John Doe", "123456", Role.STUDENT);
        User User2 = new User("Jane Doe", "654321", Role.STUDENT);
        User Alban = new User("Alban Falcoz", "140403", Role.STUDENT);
        User Theo = new User("Théo Lassauniere", "141103", Role.STUDENT);
        users.put(User1.getUserId(), User1);
        users.put(User2.getUserId(), User2);
        users.put(Alban.getUserId(), Alban);
        users.put(Theo.getUserId(), Theo);
    }

    public List<User> findAll() {
        return List.of((User) users.values());
    }

    public Optional<User> findByName(String name) {
        return users.values().stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    public Optional<User> findById(String userId) {
        return Optional.empty();
    }

    public void addUser(String userId, String userName, Role role) {
        users.computeIfAbsent(userId, k -> new User(userId, userName, role));
    }

    public void removeUser(String userId) {
        users.remove(userId);
    }

    public void updateUser(String userId, User user) {
        if (users.containsKey(userId)) {
            users.get(userId).setName(user.getName());
        }
    }
}

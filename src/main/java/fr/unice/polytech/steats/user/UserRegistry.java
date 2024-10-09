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

public class UserRegistry implements UserManager, UserFinder<User> {

    Map<String, User> users = new HashMap<>();

    public UserRegistry() {
        User User1 = new User("John Doe", "123456", Role.STUDENT);
        User User2 = new User("Jane Doe", "654321", Role.STUDENT);
        users.put(User1.getUserId(), User1);
        users.put("654321", User2);
    }

    @Override
    public List<User> findAll() {
        return List.of(users.values().toArray(new User[0]));
    }

    @Override
    public Optional<User> findByName(String name) {
        return users.values().stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.empty();
    }

    @Override
    public void addUser(String userId, String userName) {
        User user = new User(userName, userId, Role.STUDENT);
        users.put(userId, user);
    }

    @Override
    public void removeUser(String userId) {
        users.remove(userId);
    }

    @Override
    public void updateUser(String userId, String userName) {
        if (users.get(userId) != null)
            users.get(userId).setName(userName);
        else {
            User user = new User(userName, userId, Role.STUDENT);
            users.put(userId, user);
        }
    }
}

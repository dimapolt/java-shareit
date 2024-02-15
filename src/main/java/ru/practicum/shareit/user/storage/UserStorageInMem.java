package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStorageInMem {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;


    public User createUser(User user) {
        user.setId(++id);
        users.put(id, user);

        return user;
    }

    public User getUser(Long id) {
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(Long id, User user) {
        users.put(id, user);
        return user;
    }

    public User deleteUser(Long id) {
        return users.remove(id);
    }
}

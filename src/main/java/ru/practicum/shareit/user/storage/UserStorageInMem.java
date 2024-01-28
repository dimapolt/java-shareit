package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserStorageInMem implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;


    @Override
    public User createUser(User user) {
        user.setId(++id);
        users.put(id, user);

        return user;
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(Long id, User user) {
        users.put(id, user);
        return user;
    }

    @Override
    public User deleteUser(Long id) {
        return users.remove(id);
    }
}

package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User getUser(Long id);

    List<User> getAllUsers();

    User updateUser(Long id, User user);

    User deleteUser(Long id);

}

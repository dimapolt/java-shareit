package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUser(Long id);

    List<User> getAllUsers();

    User updateUser(Long id, User newUser);

    String deleteUser(Long id);
}

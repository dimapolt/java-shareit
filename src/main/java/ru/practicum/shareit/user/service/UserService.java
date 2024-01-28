package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(User user);

    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, User newUser);

    String deleteUser(Long id);
}

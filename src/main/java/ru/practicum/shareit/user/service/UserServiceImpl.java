package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.DtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.Validator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final Validator validator;
    private final DtoMapper dtoMapper;

    @Override
    public UserDto createUser(User user) {
        validator.checkEmailOnExistAndValid(user, userStorage.getAllUsers());
        User userReturn = userStorage.createUser(user);

        return dtoMapper.toDto(userReturn);
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userStorage.getUser(id);
        validator.checkOnExist(id, user);

        return dtoMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userStorage.getAllUsers();

        return users.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, User newUser) {
        User oldUser = userStorage.getUser(id);
        newUser.setId(id);
        validator.checkEmailOnExistAndValid(newUser, userStorage.getAllUsers());

        if (newUser.getName() == null  || newUser.getName().isBlank()) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getEmail() == null || newUser.getName().isBlank()) {
            newUser.setEmail(oldUser.getEmail());
        }

        User userUpdate = userStorage.updateUser(id, newUser);

        return dtoMapper.toDto(userUpdate);
    }

    @Override
    public String deleteUser(Long id) {
        User user = userStorage.deleteUser(id);
        validator.checkOnExist(id, user);

        return String.format("Удалён пользователь с id = %d", id);
    }

}

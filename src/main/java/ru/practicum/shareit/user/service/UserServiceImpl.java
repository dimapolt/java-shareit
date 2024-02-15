package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.Validator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final Validator validator;

    @Override
    public User createUser(User user) {
        User userReturn;
        try {
            userReturn = userStorage.save(user);
        } catch (RuntimeException exception) {
            throw new AlreadyExistException(String.format("Адрес электронной почты '%s' занят!", user.getEmail()));
        }

        return userReturn;
    }

    @Override
    public User getUser(Long id) {
        Optional<User> userO =  userStorage.findById(id);

        if (userO.isEmpty()) {
            throw new NoDataFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        return userO.get();
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public User updateUser(Long id, User newUser) {
        User oldUser = getUser(id);
        newUser.setId(id);
        validator.checkEmailOnExistAndValid(newUser, userStorage.findAll());

        if (newUser.getName() == null  || newUser.getName().isBlank()) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getEmail() == null || newUser.getName().isBlank()) {
            newUser.setEmail(oldUser.getEmail());
        }

        return userStorage.save(newUser);
    }

    @Override
    public String deleteUser(Long id) {
        User user = getUser(id);

        userStorage.delete(user);
        return String.format("Удалён пользователь с id = %d", id);
    }

}

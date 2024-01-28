package ru.practicum.shareit.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.MethodNotAllowedException;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class Validator {

    public void checkOnExist(Long id, User user) {
        if (user == null) {
            throw new NoDataFoundException(String.format("Пользователь с id = %d не найден!", id));
        }
    }

    public void checkOnExist(Long id, Item item) {
        if (item == null) {
            throw new NoDataFoundException(String.format("Позиция с id = %d не найдена!", id));
        }
    }

    public void checkOwner(ItemDto itemDto, UserDto user) {
        if (!itemDto.getOwner().getId().equals(user.getId())) {
            throw new NoDataFoundException(String.format("Данный пользователь не является владельцем '%s'",
                    itemDto.getName()));
        }
    }

    public void checkId(Long[] args) {
        Optional<Long> longO = Arrays.stream(args).filter(Objects::isNull).findFirst();

        if (longO.isPresent()) {
            throw new MethodNotAllowedException("Пустой id");
        }
    }

    /**
     * Проверка при создании нового пользователя и обновлении на уникальность электронного адреса
     */
    public void checkEmailOnExistAndValid(User user, List<User> users) {
        String email = user.getEmail();

        if (email != null) {
            checkEmailOnValid(email); // Проверка "валидности" email
        }

        Long id = user.getId();


        Optional<User> userO = users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();

        // Если user с таким email есть в базе и это не он сам
        if (userO.isPresent() && !userO.get().getId().equals(id)) {
            throw new AlreadyExistException(String.format("Адрес электронной почты '%s' занят!", email));
        }
    }

    /**
     * Проверка адреса электронной почты на "валидность"
     */
    private void checkEmailOnValid(String email) {
        Pattern pattern = Pattern.compile("\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*\\.\\w{2,4}");
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches() || email.isBlank()) {
           throw new ValidationException("При обновлении указан неверный адрес электронной почты!");
        }
    }

}

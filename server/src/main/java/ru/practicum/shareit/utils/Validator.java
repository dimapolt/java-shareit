package ru.practicum.shareit.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class Validator {

    public void checkOwner(Item item, User user) {
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new NoDataFoundException(String.format("Данный пользователь не является владельцем '%s'",
                    item.getName()));
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

    /**
     * Проверка бронирования по нескольким параметрам
     */
    public void checkBooking(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Недоступно для брони!");
        }

        // проверяем, что владелец и бранирующий не один и тот же человек
        checkOwnerAndBooker(booking);
        // проверка времени бронирования на корректность
        checkTimeOfBooking(booking);
    }

    private void checkOwnerAndBooker(Booking booking) {
        long ownerId = booking.getItem().getOwner().getId(); // id владельца
        long bookerId = booking.getBooker().getId(); // id бронирующего

        if (ownerId == bookerId) {
            throw new NoDataFoundException("Владелец бронирует свою вещь!");
        }
    }

    private void checkTimeOfBooking(Booking booking) {
        LocalDateTime now = LocalDateTime.now();

        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new ValidationException("Не указано время начала и/или окончания брони");
        }

        if (booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isBefore(now)
                || booking.getEnd().equals(booking.getStart())) {
            throw new ValidationException("Время окончания брони не может равняться " +
                    "или быть после времени начала брони");
        }
    }

}
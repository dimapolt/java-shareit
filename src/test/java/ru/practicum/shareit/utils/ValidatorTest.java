package ru.practicum.shareit.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.MethodNotAllowedException;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorTest {

    private final Validator validator = new Validator();
    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@user.com");
        item = new Item(1L, "Item", "Description", true, user, null);
        booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MIN.plusDays(1L),
                item, user, BookingStatus.WAITING);
    }

    @Test
    void checkOwner_userIsOwner_thenOk() {
        validator.checkOwner(item, user);
    }

    @Test
    void checkOwner_userNotOwner_noDataFoundExceptionThrown() {
        item.setOwner(new User(2L, "Another user", "another@user.com"));

        NoDataFoundException exception = assertThrows(NoDataFoundException.class,
                () -> validator.checkOwner(item, user));

        assertEquals("Данный пользователь не является владельцем 'Item'", exception.getMessage());
    }

    @Test
    void checkId_ValidIds_thenOk() {
        validator.checkId(new Long[]{1L, 10L, 1000L});
    }

    @Test
    void checkId_ValidIds_methodNotAllowedExceptionThrown() {
        MethodNotAllowedException exception = assertThrows(MethodNotAllowedException.class,
                () -> validator.checkId(new Long[]{1L, 0L, 1000L}));

        assertEquals("Пустой id", exception.getMessage());
    }

    @Test
    void checkBooking_whenValid_thenOk() {
        item.setOwner(new User(2L, "Another user", "another@user.com"));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        validator.checkBooking(booking);
    }

    @Test
    void checkBooking_whenNotAvailable_validationExceptionThrown() {
        booking.getItem().setAvailable(false);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.checkBooking(booking));

        assertEquals("Недоступно для брони!", exception.getMessage());
    }

    @Test
    void checkBooking_whenBookerIsOwner_thenOk() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        NoDataFoundException exception = assertThrows(NoDataFoundException.class,
                () -> validator.checkBooking(booking));

        assertEquals("Владелец бронирует свою вещь!", exception.getMessage());
    }

    @Test
    void checkBooking_whenNoStart_validationExceptionThrown() {
        item.setOwner(new User(2L, "Another user", "another@user.com"));
        booking.setStart(null);
        booking.setEnd(LocalDateTime.now().plusDays(2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.checkBooking(booking));

        assertEquals("Не указано время начала и/или окончания брони", exception.getMessage());
    }

    @Test
    void checkBooking_whenNoEnd_validationExceptionThrown() {
        item.setOwner(new User(2L, "Another user", "another@user.com"));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.checkBooking(booking));

        assertEquals("Не указано время начала и/или окончания брони", exception.getMessage());
    }

    @Test
    void checkBooking_whenEndBeforeStart_validationExceptionThrown() {
        item.setOwner(new User(2L, "Another user", "another@user.com"));
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.checkBooking(booking));

        assertEquals("Время окончания брони не может равняться " +
                "или быть после времени начала брони", exception.getMessage());
    }

    @Test
    void checkBooking_whenStartBeforeNow_validationExceptionThrown() {
        item.setOwner(new User(2L, "Another user", "another@user.com"));
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.checkBooking(booking));

        assertEquals("Время окончания брони не может равняться " +
                "или быть после времени начала брони", exception.getMessage());
    }

    @Test
    void checkBooking_whenEndEqualsStart_validationExceptionThrown() {
        item.setOwner(new User(2L, "Another user", "another@user.com"));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.checkBooking(booking));

        assertEquals("Время окончания брони не может равняться " +
                "или быть после времени начала брони", exception.getMessage());
    }

    @Test
    void checkEmailOnExistAndValid_whenValid_thenOk() {
        validator.checkEmailOnExistAndValid(user, new ArrayList<>());
    }

    @Test
    void checkEmailOnExistAndValid_whenEmailNoValid_validationExceptionThrown() {
        user.setEmail("user");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.checkEmailOnExistAndValid(user, new ArrayList<>()));

        assertEquals("При обновлении указан неверный адрес электронной почты!", exception.getMessage());
    }

    @Test
    void checkEmailOnExistAndValid_whenEmailOccupied_alreadyExistExceptionThrown() {
        List<User> users = List.of(new User(2L, "user2", "user@user.com"));

        AlreadyExistException exception = assertThrows(AlreadyExistException.class,
                () -> validator.checkEmailOnExistAndValid(user, users));

        assertEquals("Адрес электронной почты 'user@user.com' занят!", exception.getMessage());
    }

    @Test
    void checkEmailOnExistAndValid_whenUsersEmail_thenOk() {
        List<User> users = List.of(user, new User(2L, "user2", "user2@user.com"));

        validator.checkEmailOnExistAndValid(user, users);
    }
}
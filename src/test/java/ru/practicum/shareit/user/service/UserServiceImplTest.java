package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.Validator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserStorage userStorage;
    @Mock
    private Validator validator;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;


    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void createUser_always_saveUser() {
        when(userStorage.save(user)).thenReturn(user);

        User returned = userService.createUser(user);

        assertEquals(user, returned);
        verify(userStorage).save(any());
    }

    @Test
    void createUser_whenEmailOccupied_alreadyExistExceptionThrown() {
        user.setEmail("user@user.com");
        when(userStorage.save(user)).thenThrow(RuntimeException.class);

        AlreadyExistException exception = assertThrows(AlreadyExistException.class,
                () -> userService.createUser(user));

        assertEquals("Адрес электронной почты 'user@user.com' занят!", exception.getMessage());
        verify(userStorage).save(any());
    }

    @Test
    void getUser_whenFound_thenReturnUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        User returned = userService.getUser(1L);

        assertEquals(user, returned);
        verify(userStorage).findById(anyLong());
    }

    @Test
    void getUser_whenNotFound_noDataFoundExceptionThrown() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NoDataFoundException exception = assertThrows(NoDataFoundException.class,
                () -> userService.getUser(1L));

        assertEquals("Пользователь с id=1 не найден", exception.getMessage());
        verify(userStorage).findById(anyLong());
    }

    @Test
    void getAllUsers_always_InvokeStorageMethod() {
        userService.getAllUsers();

        verify(userStorage).findAll();
    }

    @Test
    void updateUser_whenInvoke_updateOnly2Fields() {
        User oldUser = new User(1L, "User", "user@user.com");
        User newUser = new User(2L, "Update", "update@update.com");
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(oldUser));

        userService.updateUser(1L, newUser);

        verify(userStorage).save(userArgumentCaptor.capture());
        User saved = userArgumentCaptor.getValue();

        assertEquals(saved.getId(), 1L);
        assertEquals(saved.getName(), "Update");
        assertEquals(saved.getEmail(), "update@update.com");
    }

    @Test
    void deleteUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        assertEquals("Удалён пользователь с id = 1", userService.deleteUser(1L));
    }
}
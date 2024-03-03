package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserStorageTest {

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "User", "user@user.com");
        userStorage.save(user);
    }

    @Test
    void getUserTest() {
        Optional<User> returnedOptional = userStorage.findById(1L);

        assertTrue(returnedOptional.isPresent());
        assertEquals("User", returnedOptional.get().getName());
        assertEquals("user@user.com", returnedOptional.get().getEmail());
    }

    @Test
    void getAllUsers() {
        List<User> users = userStorage.findAll();

        assertFalse(users.isEmpty());
    }

    @Test
    void updateUser() {
        User updateUser = new User(1L, "update", "update@user.com");
        userStorage.save(updateUser);

        Optional<User> returnedOptional = userStorage.findById(1L);

        assertTrue(returnedOptional.isPresent());
        assertEquals("update", returnedOptional.get().getName());
        assertEquals("update@user.com", returnedOptional.get().getEmail());
    }

    @Test
    void deleteUser() {
        User user = userStorage.findById(1L).get();
        userStorage.delete(user);

        Optional<User> userAfterDelete = userStorage.findById(1L);

        assertTrue(userAfterDelete.isEmpty());
    }

}
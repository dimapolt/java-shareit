package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemRequestStorageTest {

    @Autowired
    private ItemRequestStorage itemRequestStorage;
    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "Name", "user@user.com");
        ItemRequest itemRequest = new ItemRequest(1L, "Request", user, LocalDateTime.now());
        userStorage.save(user);
        itemRequestStorage.save(itemRequest);
    }

    @Test
    void findItemById() {
        Optional<ItemRequest> returned = itemRequestStorage.findById(1L);

        assertTrue(returned.isPresent());
    }

    @Test
    void findAllByRequestorIdNot() {
        List<ItemRequest> requests = itemRequestStorage.findAllByRequestorIdNot(3L, PageRequest.of(0, 10)).toList();

        assertFalse(requests.isEmpty());
    }

}
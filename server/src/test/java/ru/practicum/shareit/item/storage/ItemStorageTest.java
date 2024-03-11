package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemStorageTest {

    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemRequestStorage itemRequestStorage;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "User", "user@user.com");
        ItemRequest itemRequest = new ItemRequest(1L, "Request", user, LocalDateTime.now());
        Item item = new Item(1L, "Item", "Description", true, user, 1L);
        userStorage.save(user);
        itemRequestStorage.save(itemRequest);
        itemStorage.save(item);
    }

    @Test
    void findAllByOwnerId() {
        List<Item> items = itemStorage.findAllByOwnerId(1L, PageRequest.of(0, 10));

        assertFalse(items.isEmpty());
    }

    @Test
    void findAllByRequestIdIn() {
        List<Item> items = itemStorage.findAllByRequestIdIn(List.of(1L));

        assertFalse(items.isEmpty());
    }

    @Test
    void findAll() {
        List<Item> items = itemStorage.findAll();

        assertFalse(items.isEmpty());
    }
}
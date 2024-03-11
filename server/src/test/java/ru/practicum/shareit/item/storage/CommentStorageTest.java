package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CommentStorageTest {

    @Autowired
    private CommentStorage commentStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;

    @Test
    void findAllByItemId() {
        User user = new User(1L, "User", "user@user.com");
        Item item = new Item(1L, "Item", "Description", true, user, null);
        Comment comment = new Comment(1L, "Comment", item, user, LocalDateTime.now());
        userStorage.save(user);
        itemStorage.save(item);
        commentStorage.save(comment);

        List<Comment> comments = commentStorage.findAllByItemId(1L);

        Assertions.assertFalse(comments.isEmpty());
    }
}
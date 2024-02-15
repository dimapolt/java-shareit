package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item getItem(Long id);

    List<Item> getAllByUser(Long userId);

    List<Item> getAllItems();

    Item updateItem(Item item);

    String deleteItem(Long itemId);

    Comment createComment(Comment comment);

    List<Comment> getCommentsByItem(Long id);
}

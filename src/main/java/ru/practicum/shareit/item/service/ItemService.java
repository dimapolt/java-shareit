package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item getItem(Long id);

    List<Item> getAllByUser(Long userId, Pageable pageable);

    List<Item> getAllByRequestsId(List<Long> requestsId);

    List<Item> getAllItems(Pageable pageable);

    Item updateItem(Item item);

    String deleteItem(Long itemId);

    Comment createComment(Comment comment);

    List<Comment> getCommentsByItem(Long id);
}

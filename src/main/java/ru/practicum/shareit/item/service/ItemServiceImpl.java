package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final CommentStorage commentStorage;

    @Override
    public Item createItem(Item item) {
        return itemStorage.save(item);
    }

    @Override
    public Item getItem(Long id) {
        Optional<Item> itemO = itemStorage.findById(id);

        if (itemO.isEmpty()) {
            throw new NoDataFoundException(String.format("Позиция с id = %d не найдена!", id));
        }

        return itemO.get();
    }

    @Override
    public List<Item> getAllByUser(Long userId) {
        return itemStorage.findAllByOwnerId(userId);
    }

    @Override
    public List<Item> getAllItems() {
        return itemStorage.findAll();
    }

    @Override
    public Item updateItem(Item item) {
        Item oldItem = getItem(item.getId());

        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (!Objects.equals(item.getAvailable(), null)) {
            oldItem.setAvailable(item.getAvailable());
        }

        return itemStorage.save(oldItem);
    }

    @Override
    public String deleteItem(Long itemId) {
        Item item = getItem(itemId);
        itemStorage.delete(item);

        return String.format("Удалена позиция с id = %d", itemId);
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentStorage.save(comment);
    }

    @Override
    public List<Comment> getCommentsByItem(Long id) {
        return commentStorage.findAllByItemId(id);
    }
}
package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item getItem(Long id);

    List<Item> getAllItems();

    Item updateItem(Item item);

    Item deleteItem(Long itemId);
}

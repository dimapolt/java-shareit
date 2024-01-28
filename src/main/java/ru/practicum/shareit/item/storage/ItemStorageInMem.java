package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemStorageInMem implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item deleteItem(Long itemId) {
        return items.remove(itemId);
    }
}

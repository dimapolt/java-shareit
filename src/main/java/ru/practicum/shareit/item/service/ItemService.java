package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Item item);

    ItemDto getItem(Long id);

    List<ItemDto> getAllItems();

    ItemDto updateItem(Item item);

    String deleteItem(Long itemId);

}

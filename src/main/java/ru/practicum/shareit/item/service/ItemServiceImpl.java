package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.utils.DtoMapper;
import ru.practicum.shareit.utils.Validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final Validator validator;
    private final DtoMapper dtoMapper;

    @Override
    public ItemDto createItem(Item item) {
        Item itemReturn = itemStorage.createItem(item);
        return dtoMapper.toDto(itemReturn);
    }

    @Override
    public ItemDto getItem(Long id) {
        Item itemReturn = itemStorage.getItem(id);
        validator.checkOnExist(id, itemReturn);

        return dtoMapper.toDto(itemReturn);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemStorage.getAllItems().stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Item item) {
        Item oldItem = itemStorage.getItem(item.getId());
        validator.checkOnExist(oldItem.getId(), oldItem);

        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (!Objects.equals(item.getAvailable(), null)) {
            oldItem.setAvailable(item.getAvailable());
        }

        return dtoMapper.toDto(itemStorage.updateItem(oldItem));
    }

    @Override
    public String deleteItem(Long itemId) {
        Item itemReturn = itemStorage.deleteItem(itemId);
        validator.checkOnExist(itemId, itemReturn);

        return String.format("Удалена позиция с id = %d", itemId);
    }

}
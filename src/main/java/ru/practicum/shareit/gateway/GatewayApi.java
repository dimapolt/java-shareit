package ru.practicum.shareit.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GatewayApi {
    private final UserService userService;
    private final ItemService itemService;
    private final Validator validator;

    public UserDto createUser(User user) {
        return userService.createUser(user);
    }

    public UserDto getUser(Long id) {
        return userService.getUser(id);
    }

    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    public UserDto updateUser(Long id, User newUser) {
        return userService.updateUser(id, newUser);
    }

    public String deleteUser(Long id) {
        return userService.deleteUser(id);
    }

    public ItemDto createItem(Item item, Long ownerId) {
        validator.checkId(new Long[]{ownerId});
        if (item.getAvailable() == null) {
            throw new ValidationException("Нет информации о доступности вещи!");
        }

        UserDto user = userService.getUser(ownerId);
        item.setOwner(user);

        return itemService.createItem(item);
    }

    public ItemDto getItem(Long id) {
        validator.checkId(new Long[]{id});

        return itemService.getItem(id);
    }

    public List<ItemDto> getAllByUser(Long userId) {
        userService.getUser(userId);
        validator.checkId(new Long[]{userId});

        List<ItemDto> items = itemService.getAllItems();

        return items.stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public ItemDto updateItem(Long itemId, Item item, Long ownerId) {
        item.setId(itemId);
        UserDto userDto = userService.getUser(ownerId);
        ItemDto oldItem = itemService.getItem(itemId);

        validator.checkId(new Long[]{item.getId(), ownerId});
        validator.checkOwner(oldItem, userDto);

        return itemService.updateItem(item);
    }

    public String deleteItem(Long itemId) {
        validator.checkId(new Long[]{itemId});
        return itemService.deleteItem(itemId);
    }

    public List<ItemDto> searchByName(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        String searchName = text.toLowerCase();
        List<ItemDto> items = itemService.getAllItems();
        return items.stream()
                .filter(item -> (item.getName().toLowerCase().contains(searchName)
                        || item.getDescription().toLowerCase().contains(searchName))
                        && item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

}
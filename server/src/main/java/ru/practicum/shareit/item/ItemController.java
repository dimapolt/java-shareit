package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.GatewayApi;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final GatewayApi gatewayApi;

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid Item item,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление вещи.");
        return gatewayApi.createItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoFull getItem(@PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на получение вещи по id.");
        return gatewayApi.getItem(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDtoFull> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "from",
                                                 defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size",
                                                 defaultValue = "100") @Min(1) Integer size) {
        log.info("Запрос на получение всех вещей пользователя с id=" + userId);
        return gatewayApi.getAllByUser(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody Item item,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление вещи по id.");
        return gatewayApi.updateItem(itemId, item, userId);
    }

    @DeleteMapping("/{itemId}")
    public String deleteItem(@PathVariable Long itemId) {
        log.info("Запрос на удаление вещи по id.");
        return gatewayApi.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByName(@RequestParam String text,
                                      @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", defaultValue = "100") Integer size) {
        log.info("Запрос на поиск по названию.");
        return gatewayApi.searchByName(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody @Valid Comment comment,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на создание комментария");
        return gatewayApi.createComment(userId, itemId, comment);
    }

}

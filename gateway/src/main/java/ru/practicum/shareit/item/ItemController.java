package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemValidate;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated({ItemValidate.OnCreate.class})
    public ResponseEntity<Object> createItem(@RequestBody
                                  @Valid ItemDto item,
                                             @RequestHeader("X-Sharer-User-Id")
                                  @Min(value = 1, message = "Неверный идентификатор пользователя") Long userId) {
        log.info("Запрос на добавление вещи.");
        return itemClient.createItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на получение вещи по id.");
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "from",
                                                 defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size",
                                                 defaultValue = "100") @Min(1) Integer size) {
        log.info("Запрос на получение всех вещей пользователя с id=" + userId);
        return itemClient.getAllItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto item,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление вещи по id.");
        return itemClient.updateItem(item, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId) {
        log.info("Запрос на удаление вещи по id.");
        return itemClient.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByName(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam String text,
                                      @RequestParam(value = "from",
                                              defaultValue = "0") @Min(0) Integer from,
                                      @RequestParam(value = "size",
                                              defaultValue = "100") @Min(1) Integer size) {
        log.info("Запрос на поиск по названию.");
        return itemClient.searchByName(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentDto comment,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на создание комментария");
        return itemClient.createComment(comment, itemId, userId);
    }
}

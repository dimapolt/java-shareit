package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid ItemRequestDto itemRequest) {
        log.info("Создание запроса для вещи");
        return requestClient.createRequest(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение запросов пользователя");
        return requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsByParam(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(value = "from",
                                                             defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(value = "size",
                                                             defaultValue = "100") @Min(1) Integer size) {
        log.info("Получение всех запросов постранично");
        return requestClient.getRequestsByParam(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("requestId") Long requestId) {
        log.info("Получение запроса по id");
        return requestClient.getRequest(userId, requestId);
    }

}

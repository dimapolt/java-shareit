package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.GatewayApi;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final GatewayApi gatewayApi;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody @Valid ItemRequest itemRequest) {
        log.info("Создание запроса для вещи");
        return gatewayApi.createRequest(userId, itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение запросов пользователя");
        return gatewayApi.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsByParam(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "from",
                                                                 defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(value = "size",
                                                                 defaultValue = "100") @Min(1) Integer size) {
        log.info("Получение всех запросов постранично");
        return gatewayApi.getRequestsByParam(userId,from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("requestId") Long requestId) {
        log.info("Получение запроса по id");
        return gatewayApi.getRequest(userId, requestId);
    }

}

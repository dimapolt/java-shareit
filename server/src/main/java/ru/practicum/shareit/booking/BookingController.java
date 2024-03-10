package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.GatewayApi;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final GatewayApi gatewayApi;

    @PostMapping
    public BookingDto createBooking(@RequestBody @Valid BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на бронирование");
        return gatewayApi.createBooking(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение информации о брони");
        return gatewayApi.getBooking(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatus(@PathVariable Long bookingId,
                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam("approved") Boolean approved) {
        log.info("Запрос на установление статуса");
        System.out.println("-----------------" + approved);
        return gatewayApi.setStatus(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(name = "state", required = false,
                                                        defaultValue = "ALL") String state,
                                                @RequestParam(value = "from",
                                                        defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(value = "size",
                                                        defaultValue = "100") @Min(1) Integer size) {
        log.info("Запрос на получение бронирования пользователя");
        return gatewayApi.getAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "state", required = false,
                                                         defaultValue = "ALL") String state,
                                                 @RequestParam(value = "from",
                                                         defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(value = "size",
                                                         defaultValue = "100") @Min(1) Integer size) {
        log.info("Запрос на получение бронирования для владельца");
        return gatewayApi.getAllBookingByOwner(userId, state, from, size);
    }
}

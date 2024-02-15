package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.GatewayApi;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final GatewayApi gatewayApi;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto,
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
        return gatewayApi.setStatus(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(name = "state", required = false,
                                                        defaultValue = "ALL") String state) {
        log.info("Запрос на получение бронирования пользователя");
        return gatewayApi.getAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "state", required = false,
                                                         defaultValue = "ALL") String state) {
        log.info("Запрос на получение бронирования для владельца");
        return gatewayApi.getAllBookingByOwner(userId, state);
    }
}

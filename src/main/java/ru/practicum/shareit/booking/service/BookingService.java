package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.NoDataFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingStorage bookingStorage;

    @Transactional
    public Booking createBooking(Booking booking) {
        return bookingStorage.save(booking);
    }

    @Transactional
    public Booking getBooking(Long id) {
        Optional<Booking> bookingO = bookingStorage.findById(id);

        if (bookingO.isEmpty()) {
            throw new NoDataFoundException("Информации о бронировании не найдено");
        }

        return bookingO.get();
    }

    @Transactional
    public List<Booking> getAllBookingByUser(Long userId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        now, now);
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(userId,
                        now, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookings;
    }

    @Transactional
    public List<Booking> getAllBookingByOwner(Long userId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        now, now);
                break;
            case PAST:
                bookings = bookingStorage.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(userId,
                        now, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookings;
    }

    @Transactional
    public Booking getLastOrNext(Long itemId, String flag) {
        LocalDateTime now = LocalDateTime.now();

        if (flag.equals("last")) {
            return bookingStorage.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId,
                    now,
                    BookingStatus.APPROVED);
        } else if (flag.equals("next")) {
            return bookingStorage.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(),
                    BookingStatus.APPROVED);
        }
        return new Booking();
    }

    @Transactional
    public Optional<Booking> getBookingByUserAndItem(Long userId, Long itemId) {
        return bookingStorage.findFirstByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
    }
}

package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    /**
     * Booker
     */
    // ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    // CURRENT
    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId,
                                                                                 LocalDateTime start, LocalDateTime end);

    // PAST
    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    // FUTURE
    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    // WAITING
    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(Long userId, LocalDateTime now,
                                                                            BookingStatus status);

    // REJECTED
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    /**
     * Owner
     */
    // ALL
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    // CURRENT
    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId,
                                                                                    LocalDateTime start, LocalDateTime end);

    // PAST
    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    // FUTURE
    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    // WAITING
    List<Booking> findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(Long ownerId,
                                                                               LocalDateTime now, BookingStatus status);

    // REJECTED
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    /**
     * For items
     */

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now,
                                                                   BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now,
                                                                   BookingStatus status);

    /**
     * For comments
     */
    Optional<Booking> findFirstByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}
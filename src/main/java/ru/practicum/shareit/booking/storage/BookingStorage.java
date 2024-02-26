package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    // ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    // CURRENT
    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime start,
                                                                                 LocalDateTime end, Pageable pageable);

    // PAST
    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    // FUTURE
    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    // WAITING
    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(Long userId, LocalDateTime now,
                                                                            BookingStatus status, Pageable pageable);

    // REJECTED
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    // ALL
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    // CURRENT
    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start,
                                                                                    LocalDateTime end,
                                                                                    Pageable pageable);

    // PAST
    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now,
                                                                     Pageable pageable);

    // FUTURE
    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now,
                                                                      Pageable pageable);

    // WAITING
    List<Booking> findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(Long ownerId, LocalDateTime now,
                                                                               BookingStatus status, Pageable pageable);

    // REJECTED
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    List<Booking> findFirstByItemIdInAndStartBeforeAndStatusOrderByEndDesc(List<Long> itemsId, LocalDateTime now,
                                                                     BookingStatus status);

    List<Booking> findFirstByItemIdInAndStartAfterAndStatusOrderByStartAsc(List<Long> itemsId, LocalDateTime now,
                                                                     BookingStatus status);

    Optional<Booking> findFirstByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}
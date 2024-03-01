package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/data_test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookingStorageTest {

    @Autowired
    BookingStorage bookingStorage;

    private Pageable pageable;
    private LocalDateTime moment;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        moment = LocalDateTime.of(2024, 2, 16, 4, 0);
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> bookings = bookingStorage.findAllByBookerIdOrderByStartDesc(1L, pageable);

        assertEquals(5, bookings.size());
        assertEquals("2024-02-17T03:31:49", bookings.get(0).getStart().toString());

    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(1L,
                        moment,
                        moment,
                        pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(1L, moment, pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStartIsAfterOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByBookerIdAndStartIsAfterOrderByStartDesc(1L, moment, pageable);

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(1L, moment,
                        BookingStatus.WAITING, pageable);

        assertEquals(0, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED, pageable);

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByItemOwnerIdOrderByStartDesc(1L, pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(1L, moment,
                        moment, pageable);

        assertEquals(0, bookings.size());

        bookings =
                bookingStorage.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(4L, moment,
                        moment, pageable);

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(1L, moment, pageable);

        assertEquals(0, bookings.size());

        bookings =
                bookingStorage.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(2L, moment, pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(1L, moment, pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(1L,
                        moment, BookingStatus.WAITING, pageable);

        assertEquals(0, bookings.size());
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> bookings =
                bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED, pageable);

        assertEquals(1, bookings.size());
    }

    @Test
    void findFirstByItemIdInAndStartBeforeAndStatusOrderByEndDesc() {
        List<Booking> bookings =
                bookingStorage.findFirstByItemIdInAndStartBeforeAndStatusOrderByEndDesc(List.of(1L, 2L, 3L, 4L),
                        moment, BookingStatus.APPROVED);

        assertEquals(1, bookings.size());
    }

    @Test
    void findFirstByItemIdInAndStartAfterAndStatusOrderByStartAsc() {
        List<Booking> bookings =
                bookingStorage.findFirstByItemIdInAndStartAfterAndStatusOrderByStartAsc(List.of(1L, 2L, 3L, 4L),
                        moment, BookingStatus.APPROVED);

        assertEquals(1, bookings.size());
    }

    @Test
    void findFirstByItemIdAndBookerIdAndEndBefore() {
        Optional<Booking> bookingO = bookingStorage.findFirstByItemIdAndBookerIdAndEndBefore(2L, 1L, moment);

        assertTrue(bookingO.isPresent());
        assertEquals("2024-02-16T03:31:52", bookingO.get().getEnd().toString());
    }
}
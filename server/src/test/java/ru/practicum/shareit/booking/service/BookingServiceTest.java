package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.NoDataFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingStorage bookingStorage;

    @InjectMocks
    private BookingService bookingService;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
    }

    @Test
    void createBooking_always_saveBooking() {
        when(bookingStorage.save(booking)).thenReturn(booking);

        Booking returnBooking = bookingService.createBooking(booking);

        Assertions.assertEquals(booking, returnBooking);
        verify(bookingStorage).save(any());
    }

    @Test
    void getBooking_whenBookingFound_returnedBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking bookingReturn = bookingService.getBooking(0L);

        Assertions.assertEquals(booking, bookingReturn);
        verify(bookingStorage).findById(anyLong());
    }

    @Test
    void getBooking_whenBookingNotFound_noDataFoundExceptionThrown() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.empty());

        NoDataFoundException exception = assertThrows(NoDataFoundException.class,
                () -> bookingService.getBooking(0L));

        Assertions.assertEquals("Информации о бронировании не найдено", exception.getMessage());
        verify(bookingStorage).findById(anyLong());
    }

    @Test
    void getAllBookingByUser_whenAll_thenInvokeAppropriate() {
        bookingService.getAllBookingByUser(1L, BookingState.ALL, PageRequest.of(0, 10));
        verify(bookingStorage).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByUser_whenCurrent_thenInvokeAppropriate() {
        bookingService.getAllBookingByUser(1L, BookingState.CURRENT, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByUser_whenPast_thenInvokeAppropriate() {
        bookingService.getAllBookingByUser(1L, BookingState.PAST, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByUser_whenFuture_thenInvokeAppropriate() {
        bookingService.getAllBookingByUser(1L, BookingState.FUTURE, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage).findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByUser_whenWaiting_thenInvokeAppropriate() {
        bookingService.getAllBookingByUser(1L, BookingState.WAITING, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage).findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByUser_whenRejected_thenInvokeAppropriate() {
        bookingService.getAllBookingByUser(1L, BookingState.REJECTED, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByOwner_whenAll_thenInvokeAppropriate() {
        bookingService.getAllBookingByOwner(1L, BookingState.ALL, PageRequest.of(0, 10));
        verify(bookingStorage).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByOwner_whenCurrent_thenInvokeAppropriate() {
        bookingService.getAllBookingByOwner(1L, BookingState.CURRENT, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByOwner_whenPast_thenInvokeAppropriate() {
        bookingService.getAllBookingByOwner(1L, BookingState.PAST, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByOwner_whenFuture_thenInvokeAppropriate() {
        bookingService.getAllBookingByOwner(1L, BookingState.FUTURE, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByOwner_whenWaiting_thenInvokeAppropriate() {
        bookingService.getAllBookingByOwner(1L, BookingState.WAITING, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage).findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getAllBookingByOwner_whenRejected_thenInvokeAppropriate() {
        bookingService.getAllBookingByOwner(1L, BookingState.REJECTED, PageRequest.of(0, 10));
        verify(bookingStorage, never()).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(), any());
        verify(bookingStorage, never()).findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
        verify(bookingStorage).findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getLastOrNext_whenLast_thenInvokeAppropriate() {
        bookingService.getLastOrNext(List.of(1L), "last");

        verify(bookingStorage).findFirstByItemIdInAndStartBeforeAndStatusOrderByEndDesc(any(), any(), any());
        verify(bookingStorage, never()).findFirstByItemIdInAndStartAfterAndStatusOrderByStartAsc(any(), any(), any());
    }

    @Test
    void getLastOrNext_whenNext_thenInvokeAppropriate() {
        bookingService.getLastOrNext(List.of(1L), "next");

        verify(bookingStorage, never()).findFirstByItemIdInAndStartBeforeAndStatusOrderByEndDesc(any(), any(), any());
        verify(bookingStorage).findFirstByItemIdInAndStartAfterAndStatusOrderByStartAsc(any(), any(), any());
    }

    @Test
    void getLastOrNext_whenNoValid_thenNotInvoke() {
        bookingService.getLastOrNext(List.of(1L), "wrong");

        verify(bookingStorage, never()).findFirstByItemIdInAndStartBeforeAndStatusOrderByEndDesc(any(), any(), any());
        verify(bookingStorage, never()).findFirstByItemIdInAndStartAfterAndStatusOrderByStartAsc(any(), any(), any());
    }

    @Test
    void getBookingByUserAndItem() {
        bookingService.getBookingByUserAndItem(1L, 1L);
        verify(bookingStorage).findFirstByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any());
    }
}
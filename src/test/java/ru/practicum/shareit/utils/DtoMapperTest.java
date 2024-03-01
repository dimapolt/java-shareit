package ru.practicum.shareit.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoMapperTest {
    private final DtoMapper dtoMapper = new DtoMapper();
    private User user;
    private Item item;
    private ItemDto itemDto;
    private UserDto userDto;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@user.com");
        userDto = new UserDto(1L, "User", "user@user.com");
        item = new Item(1L, "Item", "Description", true, user, null);
        itemDto = new ItemDto(1L, "Item", "Description", true, userDto, null);
        booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MIN.plusDays(1L),
                item, user, BookingStatus.WAITING);
        bookingDto = new BookingDto(1L, LocalDateTime.MIN, LocalDateTime.MIN.plusDays(1L),
                itemDto.getId(), itemDto, userDto, userDto.getId(), BookingStatus.WAITING);
    }

    @Test
    void fromUserToUserDtoTest() {
        UserDto expected = userDto;

        UserDto actual = dtoMapper.toDto(user);

        assertEquals(expected, actual);
    }

    @Test
    void fromItemToItemDto() {
        ItemDto expected = itemDto;

        ItemDto actual = dtoMapper.toDto(item);

        assertEquals(expected, actual);
    }

    @Test
    void fromBookingToBookingDto() {
        BookingDto expected = bookingDto;

        BookingDto actual = dtoMapper.toDto(booking);

        assertEquals(expected, actual);
    }

    @Test
    void fromBookingDtoToBooking() {
        Booking expected = booking;
        expected.setItem(null);
        expected.setBooker(null);

        Booking actual = dtoMapper.toEntity(bookingDto);

        assertEquals(expected, actual);
    }

    @Test
    void toItemDtoFull() {
        ItemDtoFull expected = new ItemDtoFull(1L, "Item", "Description", true, null, null, new ArrayList<>());

        ItemDtoFull actual = dtoMapper.toItemDtoFull(item, null, null, new ArrayList<>());

        assertEquals(expected, actual);
    }

    @Test
    void fromCommentToCommentDto() {
        CommentDto expected = new CommentDto(1L, "Comment", "User", LocalDateTime.MIN);
        Comment comment = new Comment(1L, "Comment", item, user, LocalDateTime.MIN);

        CommentDto actual = dtoMapper.toDto(comment);

        assertEquals(expected, actual);
    }

    @Test
    void toItemRequestDto() {
        ItemRequestDto expected = new ItemRequestDto(1L, "Request", LocalDateTime.MIN, new ArrayList<>());
        ItemRequest itemRequest = new ItemRequest(1L, "Request", user, LocalDateTime.MIN);

        ItemRequestDto actual = dtoMapper.toDto(itemRequest, new ArrayList<>());

        assertEquals(expected, actual);
    }
}
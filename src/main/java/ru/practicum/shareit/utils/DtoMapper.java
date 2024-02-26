package ru.practicum.shareit.utils;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {
    public UserDto toDto(User user) {
        return new UserDto(user.getId(),
                user.getName(),
                user.getEmail());
    }


    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                toDto(item.getOwner()),
                item.getRequestId());
    }


    public BookingDto toDto(Booking booking) {
        UserDto booker = toDto(booking.getBooker());
        ItemDto item = toDto(booking.getItem());

        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                item,
                booker,
                booker.getId(),
                booking.getStatus());
    }

    public Booking toEntity(BookingDto bookingDto) {

        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                null,
                null,
                bookingDto.getStatus());
    }

    public ItemDtoFull toItemDtoFull(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        BookingDto lastBookingDto;
        BookingDto nextBookingDto;

        if (lastBooking == null) {
            lastBookingDto = null;
        } else {
            lastBookingDto = toDto(lastBooking);
        }

        if (nextBooking == null) {
            nextBookingDto = null;
        } else {
            nextBookingDto = toDto(nextBooking);
        }

        List<CommentDto> commentsDto = comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new ItemDtoFull(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingDto,
                nextBookingDto,
                commentsDto);
    }

    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public ItemRequestDto toDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemRequestDto.ItemDto> itemsDto = items.stream()
                .map(item -> new ItemRequestDto.ItemDto(item.getId(),
                                                                  item.getName(),
                                                                  item.getDescription(),
                                                                  item.getAvailable(),
                                                          item.getRequestId()))
                .collect(Collectors.toList());

        return new ItemRequestDto(itemRequest.getId(),
                                  itemRequest.getDescription(),
                                  itemRequest.getCreated(),
                                  itemsDto);
    }

}

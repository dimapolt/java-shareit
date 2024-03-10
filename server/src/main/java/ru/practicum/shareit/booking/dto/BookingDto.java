package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull
    private Long itemId;
    private ItemDto item;
    private UserDto booker;
    private Long bookerId;
    private BookingStatus status = BookingStatus.WAITING;
}
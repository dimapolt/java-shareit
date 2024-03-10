package ru.practicum.shareit.request.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemRequestDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class ItemDto {
        Long id;
        String name;
        String description;
        Boolean available;
        Long requestId;
    }
}

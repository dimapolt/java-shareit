package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemDto {
    @NotBlank(groups = ItemValidate.OnCreate.class, message = "Имя не может быть пустым!")
    private String name;
    @NotBlank(groups = ItemValidate.OnCreate.class, message = "Необходимо краткое описание!")
    private String description;
    @NotNull(groups = ItemValidate.OnCreate.class, message = "Нет информации о доступности!")
    private Boolean available;
    private Long requestId;
}
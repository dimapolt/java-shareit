package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto {
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @Email(message = "Неверный адрес электронной почты!")
    @NotBlank(message = "Адрес электронной почты не может быть пустым!")
    private String email;
}
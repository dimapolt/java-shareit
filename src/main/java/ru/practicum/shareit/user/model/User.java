package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @Email(message = "Неверный адрес электронной почты!")
    @NotBlank(message = "Адрес электронной почты не может быть пустым!")
    private String email;
}

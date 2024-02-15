package ru.practicum.shareit.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс содержащий сообщение о причинах исключения и код ошибки, для ответа "фронтенду"
 */
@AllArgsConstructor
@Getter
public class ErrorDescription {
    private String error;
}

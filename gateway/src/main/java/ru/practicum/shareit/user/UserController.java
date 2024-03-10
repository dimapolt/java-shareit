package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto user) {
        return userClient.createUser(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Min(value = 1,
            message = "Неверный идентификатор") Long userId) {
        log.info("Запрос на получение пользователя по id.");
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос на получение всех пользователей.");
        return userClient.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Min(value = 1,
            message = "Неверный идентификатор") Long userId,
                                             @RequestBody UserDto user) {
        log.info("Запрос на обновление пользователя.");
        return userClient.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя.");
        return userClient.deleteUser(userId);
    }

}

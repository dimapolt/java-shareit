package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.GatewayApi;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final GatewayApi gatewayApi;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto user) {
        log.info("Запрос на получение пользователя по id.");
        return gatewayApi.createUser(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя по id.");
        return gatewayApi.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение всех пользователей.");
        return gatewayApi.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody User user) {
        log.info("Запрос на обновление пользователя.");
        return gatewayApi.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto deleteUser(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя.");
        return gatewayApi.deleteUser(userId);
    }

}

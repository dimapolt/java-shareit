package ru.practicum.shareit.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GatewayApi {

    private final DtoMapper dtoMapper;
    private final UserService userService;

    public UserDto createUser(UserDto userDto) {
        User user = dtoMapper.toEntity(userDto);
        return dtoMapper.toDto(userService.createUser(user));
    }

    public UserDto getUser(Long userId) {
        return dtoMapper.toDto(userService.getUser(userId));
    }

    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(Long id, User newUser) {
        return dtoMapper.toDto(userService.updateUser(id, newUser));
    }

    public UserDto deleteUser(Long id) {
        return dtoMapper.toDto(userService.deleteUser(id));
    }
}
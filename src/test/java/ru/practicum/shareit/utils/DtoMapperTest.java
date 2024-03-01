package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoMapperTest {
    private final DtoMapper dtoMapper = new DtoMapper();

    @Test
    void fromUserToUserDtoTest() {
        User user = new User(1L, "User", "user@user.com");
        UserDto expected = new UserDto(1L, "User", "user@user.com");

        UserDto actual = dtoMapper.toDto(user);

        assertEquals(expected, actual);
    }

    @Test
    void fromItemToItemDto() {

    }

    @Test
    void testToDto1() {
    }

    @Test
    void toEntity() {
    }

    @Test
    void toItemDtoFull() {
    }

    @Test
    void testToDto2() {
    }

    @Test
    void testToDto3() {
    }
}
package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.gateway.GatewayApi;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private GatewayApi gatewayApi;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User user;
    private UserDto userDto;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        user = new User(1L, "Name", "user@ya.ru");
        userDto = new UserDto(1L, "Name", "user@ya.ru");
    }

    @Test
    @SneakyThrows
    void createUser_whenInvoked_thenStatusOkWithUserDto() {
        String userJson = objectMapper.writeValueAsString(user);
        when(gatewayApi.createUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        Assertions.assertEquals(userDto, userController.createUser(user));
    }

    @Test
    @SneakyThrows
    void createUser_whenNoValidUser_thenStatusBadRequest() {
        User emptyName = new User(0L, "", "user@user.com");
        String userJson = objectMapper.writeValueAsString(emptyName);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
        verify(gatewayApi, never()).createUser(emptyName);

        User wrongEmail = new User(0L, "name", "user.user.com");
        userJson = objectMapper.writeValueAsString(wrongEmail);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
        verify(gatewayApi, never()).createUser(wrongEmail);
    }

    @Test
    @SneakyThrows
    void getUser_whenInvoke_thenStatusOkWithUser() {
        when(gatewayApi.getUser(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.email").value("user@ya.ru"));

        verify(gatewayApi).getUser(anyLong());
    }

    @Test
    @SneakyThrows
    void getAllUsers_whenInvoked_thenStatusOkWithEmptyList() {
        when(gatewayApi.getAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(gatewayApi).getAllUsers();

        Assertions.assertEquals(new ArrayList<>(), userController.getAllUsers());
    }

    @Test
    @SneakyThrows
    void updateUser_whenInvoked_thenStatusOkWithUserDto() {
        User userUpdate = new User(1L, "Update", "user@ya.ru");
        UserDto userUpdateDto = new UserDto(1L, "Update", "user@ya.ru");
        String jsonUserUpdate = objectMapper.writeValueAsString(userUpdate);

        when(gatewayApi.updateUser(any(Long.class), any(User.class)))
                .thenReturn(userUpdateDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Update"))
                .andExpect(jsonPath("$.email").value("user@ya.ru"));
    }

    @Test
    @SneakyThrows
    void deleteUser_whenInvoked_thenStatusOk() {
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }

}
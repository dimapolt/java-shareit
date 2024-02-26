package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private GatewayApi gatewayApi;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Item item;
    private ItemDto itemDto;
    private ItemDtoFull itemDtoFull;

    @BeforeEach
    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
//        objectMapper = new ObjectMapper();
//        User user = new User(1L, "User1", "user@ya.ru");
//        UserDto userDto = new UserDto(1L, "User1", "user@ya.ru");
//        item = new Item(1L, "TestItem", "Description", true, user, new ItemRequest());
//        itemDto = new ItemDto(1L, "TestItem", "Description", true, userDto, new ItemRequest());
//        itemDtoFull = new ItemDtoFull(1L, "TestItem", "Description", true, null, null, null);
    }

    @Test
    void createItemTest() throws Exception {
        String itemJson = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk());
    }

    @Test
    void getItemTest() throws Exception {
        when(gatewayApi.getItem(1L, 1L)).thenReturn(itemDtoFull);
        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("TestItem"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void getAllItemsTest() throws Exception {
//        when(gatewayApi.getAllByUser(1L)).thenReturn(new ArrayList<>());
//        mockMvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", 1))
//                .andExpect(status().isOk());
    }

    @Test
    void updateItemTest() throws Exception {
        String itemJson = objectMapper.writeValueAsString(item);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItemTest() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchByNameTest() throws Exception {
//        when(gatewayApi.searchByName("TestItem")).thenReturn(List.of(itemDto));
//
//        mockMvc.perform(get("/items/search")
//                        .queryParam("text", "TestItem"))
//                .andExpect(status().isOk());
    }
}
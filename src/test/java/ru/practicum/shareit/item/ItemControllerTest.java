package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper = new ObjectMapper();
        User user = new User(1L, "User1", "user@ya.ru");
        UserDto userDto = new UserDto(1L, "User1", "user@ya.ru");
        item = new Item(1L, "TestItem", "Description", true, user, null);
        itemDto = new ItemDto(1L, "TestItem", "Description", true, userDto, null);
        itemDtoFull = new ItemDtoFull(1L, "TestItem", "Description", true, null, null, null);
    }

    @Test
    @SneakyThrows
    void createItem_whenInvoke_thenStatusOk() {
        String itemJson = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk());

        verify(gatewayApi).createItem(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void createItem_whenNoValidItem_thenStatusBadRequest() {
        Item withEmptyName = new Item(0L, "", "Description", null, null, null);
        String itemJson = objectMapper.writeValueAsString(withEmptyName);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isBadRequest());

        Item withEmptyDescription = new Item(0L, "Item", "", null, null, null);
        itemJson = objectMapper.writeValueAsString(withEmptyDescription);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).createItem(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void getItem_whenInvoke_thenStatusOkWithItem() {
        when(gatewayApi.getItem(anyLong(), anyLong())).thenReturn(itemDtoFull);
        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("TestItem"))
                .andExpect(jsonPath("$.description").value("Description"));

        verify(gatewayApi).getItem(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllItems_whenInvoke_thenStatusOk() {
        when(gatewayApi.getAllByUser(anyLong(), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk());

        verify(gatewayApi).getAllByUser(anyLong(), anyInt(), anyInt());

        Assertions.assertEquals(new ArrayList<>(), itemController.getAllItems(anyLong(), anyInt(), anyInt()));
    }

    @Test
    @SneakyThrows
    void updateItem_whenInvoke_thenStatusOk() {
        String itemJson = objectMapper.writeValueAsString(item);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void updateItem_withoutUser_thenStatusBadRequest() {
        String itemJson = objectMapper.writeValueAsString(item);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void deleteItem_whenInvoke_thenStatusOk() {
        mockMvc.perform(delete("/items/{itemId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void searchByName_whenInvoke_thenStatusOk() {
        when(gatewayApi.searchByName(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .queryParam("text", "TestItem")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk());
        verify(gatewayApi).searchByName(anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void createComment_whenInvoke_thenStatusOk() {
        Comment comment = new Comment(1L, "Comment", null, null, null);
        String commentJson = objectMapper.writeValueAsString(comment);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andExpect(status().isOk());

        verify(gatewayApi).createComment(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void createComment_whenNoValidComment_thenStatusBadRequest() {
        Comment commentWithoutText = new Comment(1L, "", null, null, null);
        String commentJson = objectMapper.writeValueAsString(commentWithoutText);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).createComment(anyLong(), anyLong(), any());
    }
}
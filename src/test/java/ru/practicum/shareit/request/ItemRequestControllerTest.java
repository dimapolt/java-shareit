package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private GatewayApi gatewayApi;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper = new ObjectMapper();
        itemRequest = new ItemRequest(1L, "Description", null, null);
    }

    @Test
    @SneakyThrows
    void createRequest_whenInvoke_thenStatusOk() {
        String itemReqJson = objectMapper.writeValueAsString(itemRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemReqJson))
                .andExpect(status().isOk());

        verify(gatewayApi).createRequest(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void createRequest_whenNoValid_thenStatusBadRequest() {
        itemRequest.setDescription(null);
        String itemReqJson = objectMapper.writeValueAsString(itemRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemReqJson))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).createRequest(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getRequests_whenInvoke_thenStatusOk() {
        when(gatewayApi.getRequestsByUser(anyLong())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(gatewayApi).getRequestsByUser(anyLong());
        Assertions.assertEquals(new ArrayList<>(), itemRequestController.getRequests(anyLong()));
    }

    @Test
    @SneakyThrows
    void getRequests_withoutParam_thenStatusBadRequest() {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).getRequestsByUser(anyLong());
    }

    @Test
    @SneakyThrows
    void getRequestsByParam_whenInvoke_thenStatusOk() {
        when(gatewayApi.getRequestsByParam(anyLong(), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk());

        verify(gatewayApi).getRequestsByParam(anyLong(), anyInt(), anyInt());
        Assertions.assertEquals(new ArrayList<>(), itemRequestController.getRequestsByParam(anyLong(), anyInt(), anyInt()));
    }

    @Test
    @SneakyThrows
    void getRequestsByParam_withoutParam_thenStatusBadRequest() {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).getRequestsByParam(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getRequest_whenInvoke_thenStatusOkWithItemRequest() {
        ItemRequestDto returned = new ItemRequestDto(1L, "Description", null, null);

        when(gatewayApi.getRequest(anyLong(), anyLong())).thenReturn(returned);
        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Description"));

        verify(gatewayApi).getRequest(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getRequest_withoutParam_thenStatusBadRequest() {

        mockMvc.perform(get("/requests/{requestId}", 1L))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).getRequest(anyLong(), anyLong());
    }
}
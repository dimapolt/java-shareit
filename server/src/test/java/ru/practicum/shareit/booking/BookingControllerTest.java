package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.gateway.GatewayApi;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private GatewayApi gatewayApi;
    @InjectMocks
    BookingController bookingController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
        bookingDto = new BookingDto(0L, null, null, 1L, null, null, null, BookingStatus.WAITING);
    }

    @Test
    @SneakyThrows
    void createBooking_whenInvoke_thenStatusOk() {
        String bookingJson = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isOk());

        verify(gatewayApi).createBooking(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void createBooking_whenNoValidBooking_thenStatusBadRequest() {
        bookingDto.setItemId(null);
        String bookingJson = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).createBooking(any(), anyLong());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBooking_whenInvoke_thenStatusOkWithBookingDto() {
        BookingDto expectedBookingDto = new BookingDto(0L, null, null, 1L, null, null, null, BookingStatus.WAITING);
        when(gatewayApi.getBooking(anyLong(), anyLong())).thenReturn(expectedBookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 0L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0L))
                .andExpect(jsonPath("$.itemId").value(1L));

        verify(gatewayApi).getBooking(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void setStatus_whenInvoke_thenStatusOk() {
        mockMvc.perform(patch("/bookings/{bookingId}", 0L)
                        .header("X-Sharer-User-Id", 1L)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk());

        verify(gatewayApi).setStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void setStatus_withoutApproved_thenStatusBadRequest() {
        mockMvc.perform(patch("/bookings/{bookingId}", 0L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).setStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getAllBookingByUser_whenInvoke_thenStatusOk() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "100"))
                .andExpect(status().isOk());

        verify(gatewayApi).getAllBookingByUser(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllBookingByUser_withoutParams_thenStillStatusOk() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(gatewayApi).getAllBookingByUser(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllBookingByUser_withoutUser_thenStatusBadRequest() {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).getAllBookingByUser(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllBookingByOwner_whenInvoke_thenStatusOk() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "100"))
                .andExpect(status().isOk());

        verify(gatewayApi).getAllBookingByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllBookingByOwner_withoutParams_thenStillStatusOk() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(gatewayApi).getAllBookingByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllBookingByOwner_withoutUser_thenStatusBadRequest() {
        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().isBadRequest());

        verify(gatewayApi, never()).getAllBookingByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }
}
package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestStorage requestStorage;
    @InjectMocks
    private ItemRequestService requestService;
    private ItemRequest request;
    private List<ItemRequest> requests;

    @BeforeEach
    void setUp() {
        request = new ItemRequest();
        requests = List.of(request);
    }

    @Test
    void createRequest_always_saveRequest() {
        Mockito.when(requestStorage.save(request)).thenReturn(request);

        ItemRequest returned = requestService.createRequest(request);

        assertEquals(request, returned);
        Mockito.verify(requestStorage).save(any(ItemRequest.class));
    }

    @Test
    void getRequestsByUser_whenInvoke_getRequestsList() {
        when(requestStorage.findAllByRequestorId(anyLong())).thenReturn(requests);

        List<ItemRequest> returned = requestService.getRequestsByUser(1L);

        assertEquals(requests, returned);
        verify(requestStorage).findAllByRequestorId(anyLong());
    }

    @Test
    void getRequestsByParam_whenInvoke_getRequestsList() {
        when(requestStorage.findAllByRequestorIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(requests));

        List<ItemRequest> returned = requestService.getRequestsByParam(1L, 0, 10);

        assertEquals(requests, returned);
        verify(requestStorage).findAllByRequestorIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getRequest_whenFound_returnRequest() {
        when(requestStorage.findItemById(anyLong())).thenReturn(request);

        ItemRequest returned = requestService.getRequest(1L);

        assertEquals(request, returned);
        verify(requestStorage).findItemById(anyLong());
    }

    @Test
    void getRequest_whenNotFound_noDataFoundExceptionThrown() {
        when(requestStorage.findItemById(anyLong())).thenReturn(null);

        NoDataFoundException exception = assertThrows(NoDataFoundException.class,
                () -> requestService.getRequest(1L));

        assertEquals("Нет запроса с id=1", exception.getMessage());
        verify(requestStorage).findItemById(anyLong());
    }
}
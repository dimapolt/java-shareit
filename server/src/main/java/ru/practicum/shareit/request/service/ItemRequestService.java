package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestStorage requestStorage;

    public ItemRequest createRequest(ItemRequest itemRequest) {
        return requestStorage.save(itemRequest);
    }

    public List<ItemRequest> getRequestsByUser(Long userId) {
        return requestStorage.findAllByRequestorId(userId).stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated))
                .collect(Collectors.toList());
    }

    public List<ItemRequest> getRequestsByParam(Long userId, Integer from, Integer size) {
        return requestStorage.findAllByRequestorIdNot(userId,
                PageRequest.of(from / size, size)).toList();
    }

    public ItemRequest getRequest(Long requestId) {
        ItemRequest itemRequest = requestStorage.findItemById(requestId);

        if (itemRequest == null) {
            throw new NoDataFoundException("Нет запроса с id=" + requestId);
        } else
            return itemRequest;
    }

}

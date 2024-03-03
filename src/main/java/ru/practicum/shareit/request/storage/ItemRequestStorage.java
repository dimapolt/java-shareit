package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    ItemRequest findItemById(Long id);

    Page<ItemRequest> findAllByRequestorIdNot(Long requestorId, Pageable pageable);

    List<ItemRequest> findAllByRequestorId(Long requestorId);
}

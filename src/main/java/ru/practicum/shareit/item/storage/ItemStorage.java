package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> requestsId);
    Page<Item> findAll(Pageable pageable);

}
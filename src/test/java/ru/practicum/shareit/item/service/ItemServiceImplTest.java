package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private CommentStorage commentStorage;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @BeforeEach
    void setUp() {
        item = new Item();
    }

    @Test
    void createItem_always_saveItem() {
        when(itemStorage.save(item)).thenReturn(item);

        Item returnedItem = itemService.createItem(item);

        assertEquals(item, returnedItem);
        verify(itemStorage).save(any());
    }

    @Test
    void getItem_whenFound_returnedItem() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        Item returnedItem = itemService.getItem(1L);

        assertEquals(item, returnedItem);
        verify(itemStorage).findById(1L);
    }

    @Test
    void getItem_whenNotFound_noDataFoundExceptionThrown() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        NoDataFoundException exception = assertThrows(NoDataFoundException.class,
                () -> itemService.getItem(1L));

        assertEquals("Позиция с id = 1 не найдена!", exception.getMessage());
        verify(itemStorage).findById(anyLong());
    }

    @Test
    void getAllByUser_always_InvokeStorageMethod() {
        itemService.getAllByUser(1L, PageRequest.of(0, 10));

        verify(itemStorage).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void getAllByRequestsId_always_InvokeStorageMethod() {
        itemService.getAllByRequestsId(List.of(1L));

        verify(itemStorage).findAllByRequestIdIn(any());
    }

    @Test
    void getAllItems_always_InvokeStorageMethod() {
        when(itemStorage.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of()));

        itemService.getAllItems(PageRequest.of(0, 10));
        verify(itemStorage).findAll(PageRequest.of(0, 10));
    }

    @Test
    void updateItem_whenInvoke_updateOnly3Fields() {
        Item oldItem = new Item(1L, "Old", "Description", true, null, null);
        Item newItem = new Item(5L, "New", "New description", false, new User(), 1L);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(oldItem));

        itemService.updateItem(newItem);

        verify(itemStorage).save(itemArgumentCaptor.capture());

        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals(1L, savedItem.getId());
        assertEquals("New", savedItem.getName());
        assertEquals("New description", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
        assertNull(savedItem.getOwner());
        assertNull(savedItem.getRequestId());
    }

    @Test
    void deleteItem_whenInvoke_returnStringMessage() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        assertEquals("Удалена позиция с id = 10", itemService.deleteItem(10L));
    }

    @Test
    void createComment() {
        Comment comment = new Comment();
        when(commentStorage.save(comment)).thenReturn(comment);

        Comment returnedComment = itemService.createComment(comment);

        assertEquals(comment, returnedComment);
    }

    @Test
    void getCommentsByItem() {
        Comment comment = new Comment();
        comment.setItem(item);
        when(commentStorage.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        List<Comment> comments = itemService.getCommentsByItem(1L);

        assertEquals(1, comments.size());
        verify(commentStorage).findAllByItemId(anyLong());
    }
}
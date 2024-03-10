package ru.practicum.shareit.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DtoMapper;
import ru.practicum.shareit.utils.Validator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GatewayApi {
    private final Validator validator;
    private final DtoMapper dtoMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final ItemRequestService requestService;

    public UserDto createUser(UserDto userDto) {
        User user = dtoMapper.toEntity(userDto);
        return dtoMapper.toDto(userService.createUser(user));
    }

    public UserDto getUser(Long userId) {
        return dtoMapper.toDto(userService.getUser(userId));
    }

    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(Long id, User newUser) {
        return dtoMapper.toDto(userService.updateUser(id, newUser));
    }

    public UserDto deleteUser(Long id) {
        return dtoMapper.toDto(userService.deleteUser(id));
    }

    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        if (itemDto.getRequestId() != null) {
            requestService.getRequest(itemDto.getRequestId());
        }

        Item item = dtoMapper.toEntity(itemDto);
        User user = userService.getUser(ownerId);
        item.setOwner(user);

        return dtoMapper.toDto(itemService.createItem(item));
    }

    public ItemDtoFull getItem(Long id, Long ownerId) {
        Item item = itemService.getItem(id);
        List<Comment> comments = itemService.getCommentsByItem(item.getId());

        if (comments == null) {
            comments = new ArrayList<>();
        }

        if (item.getOwner().getId().equals(ownerId)) {
            List<Booking> lastList = bookingService.getLastOrNext(List.of(item.getId()), "last");
            List<Booking> nextList = bookingService.getLastOrNext(List.of(item.getId()), "next");
            Booking last = lastList.isEmpty() ? null : lastList.get(0);
            Booking next = nextList.isEmpty() ? null : nextList.get(0);

            return dtoMapper.toItemDtoFull(item, last, next, comments);
        }

        return dtoMapper.toItemDtoFull(item, null, null, comments);
    }

    public List<ItemDtoFull> getAllByUser(Long userId, Integer from, Integer size) {
        userService.getUser(userId);

        List<Item> items = itemService.getAllByUser(userId, PageRequest.of(from / size, size));

        List<Long> itemsId = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> lastList = bookingService.getLastOrNext(itemsId, "last");
        List<Booking> nextList = bookingService.getLastOrNext(itemsId, "next");

        Map<Long, Booking> lastMap = lastList.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(),
                        booking -> booking));
        Map<Long, Booking> nextMap = nextList.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(),
                        booking -> booking));


        return items.stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> {
                    Booking last = lastMap.get(item.getId());
                    Booking next = nextMap.get(item.getId());
                    List<Comment> comments = itemService.getCommentsByItem(item.getId());
                    return dtoMapper.toItemDtoFull(item, last, next, comments);
                })
                .collect(Collectors.toList());
    }

    public ItemDto updateItem(Long itemId, Item item, Long ownerId) {
        item.setId(itemId);
        User user = userService.getUser(ownerId);
        Item oldItem = itemService.getItem(itemId);

        validator.checkOwner(oldItem, user);

        return dtoMapper.toDto(itemService.updateItem(item));
    }

    public String deleteItem(Long itemId) {
        return itemService.deleteItem(itemId);
    }

    public List<ItemDto> searchByName(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        String searchName = text.toLowerCase();
        List<Item> items = itemService.getAllItems(PageRequest.of(from / size, size));
        return items.stream()
                .filter(item -> (item.getName().toLowerCase().contains(searchName)
                        || item.getDescription().toLowerCase().contains(searchName))
                        && item.getAvailable().equals(true))
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User user = userService.getUser(userId);
        Item item = itemService.getItem(bookingDto.getItemId());
        Booking booking = dtoMapper.toEntity(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);

        validator.checkBooking(booking);

        return dtoMapper.toDto(bookingService.createBooking(booking));
    }

    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingService.getBooking(bookingId);

        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NoDataFoundException("Запрос на просмотр бронирования доступен только " +
                    "для владельца брони или владельца вещи");
        }

        return dtoMapper.toDto(booking);
    }

    public BookingDto setStatus(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingService.getBooking(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NoDataFoundException("Запрос на изменение статуса бронирования доступен только " +
                    "для владельца вещи");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Бронирование уже одобрено");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return dtoMapper.toDto(bookingService.createBooking(booking));
    }

    public List<BookingDto> getAllBookingByUser(Long userId, String state, Integer from, Integer size) {
        userService.getUser(userId); // Проверяем существует ли такой пользователь
        BookingState bookingState = BookingState.from(state).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + state));

        return bookingService.getAllBookingByUser(userId, bookingState, PageRequest.of(from / size, size)).stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getAllBookingByOwner(Long userId, String state, Integer from, Integer size) {
        userService.getUser(userId); // Проверяем существует ли такой пользователь
        BookingState bookingState = BookingState.from(state).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + state));

        return bookingService.getAllBookingByOwner(userId, bookingState, PageRequest.of(from / size, size)).stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public CommentDto createComment(Long userId, Long itemId, Comment comment) {
        Booking booking = bookingService.getBookingByUserAndItem(userId, itemId).orElseThrow(
                () -> new ValidationException(String.format("Пользователь с id=%d" +
                        " не брал вещь с id=%d", userId, itemId)));

        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Отзыв можно оставить только после окончания аренды!");
        }

        comment.setAuthor(booking.getBooker());
        comment.setItem(booking.getItem());
        comment.setCreated(LocalDateTime.now());

        return dtoMapper.toDto(itemService.createComment(comment));
    }

    public ItemRequestDto createRequest(Long userId, ItemRequest itemRequest) {
        User requester = userService.getUser(userId);
        itemRequest.setRequestor(requester);
        itemRequest.setCreated(LocalDateTime.now());
        return dtoMapper.toDto(requestService.createRequest(itemRequest), new ArrayList<>());
    }

    public List<ItemRequestDto> getRequestsByUser(Long userId) {
        userService.getUser(userId);
        List<ItemRequest> itemRequests = requestService.getRequestsByUser(userId);
        List<Long> requestsId = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemService.getAllByRequestsId(requestsId);

        return itemRequests.stream()
                .map(itemRequest -> dtoMapper.toDto(itemRequest,
                        items.stream()
                                .filter(item -> item.getRequestId().equals(itemRequest.getId()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getRequestsByParam(Long userId, Integer from, Integer size) {
        List<ItemRequest> itemRequests = requestService.getRequestsByParam(userId, from, size);
        List<Long> requestsId = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemService.getAllByRequestsId(requestsId);

        return itemRequests.stream()
                .map(itemRequest -> dtoMapper.toDto(itemRequest,
                        items.stream()
                                .filter(item -> item.getRequestId().equals(itemRequest.getId()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequest(Long userId, Long requestId) {
        userService.getUser(userId);
        ItemRequest itemRequest = requestService.getRequest(requestId);
        List<Item> items = itemService.getAllByRequestsId(List.of(requestId));
        return dtoMapper.toDto(itemRequest, items);
    }

}
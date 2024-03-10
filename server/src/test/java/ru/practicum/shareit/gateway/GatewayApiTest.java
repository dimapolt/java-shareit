package ru.practicum.shareit.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NoDataFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayApiTest {
    @Mock
    private Validator validator;
    @Mock
    private DtoMapper dtoMapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingService bookingService;
    @Mock
    private ItemRequestService requestService;

    @InjectMocks
    private GatewayApi gatewayApi;
    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Name", "user@user.com");
        item = new Item(1L, "Item", "Description", true, user, null);
        booking = new Booking();
        bookingDto = new BookingDto();
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
    }

    @Test
    void createUser_whenInvoke_thenReturnUserDto() {
        UserDto userDto = new UserDto(1L, "Name", "user@user.com");

        when(userService.createUser(any(User.class))).thenReturn(user);
        when(dtoMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto returned = gatewayApi.createUser(user);
        assertEquals(userDto, returned);
    }

    @Test
    void getUser_always_thenInvokeMethods() {
        when(userService.getUser(anyLong())).thenReturn(user);
        gatewayApi.getUser(1L);

        verify(userService).getUser(1L);
        verify(dtoMapper).toDto(any(User.class));
    }

    @Test
    void getAllUsers_always_thenInvokeMethods() {
        when(userService.getAllUsers()).thenReturn(List.of(user));
        gatewayApi.getAllUsers();

        verify(userService).getAllUsers();
        verify(dtoMapper, atLeast(1)).toDto(any(User.class));
    }

    @Test
    void updateUser_always_thenInvokeMethods() {
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(user);
        gatewayApi.updateUser(1L, user);

        verify(userService).updateUser(1L, user);
        verify(dtoMapper).toDto(any(User.class));
    }

    @Test
    void deleteUser_always_thenInvokeMethod() {
        gatewayApi.deleteUser(1L);

        verify(userService).deleteUser(anyLong());
    }

    @Test
    void createItem_whenAvailableAndNoRequestId_thenInvoke4Methods() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemService.createItem(any(Item.class))).thenReturn(item);

        gatewayApi.createItem(item, 1L);

        verify(userService).getUser(1L);
        verify(itemService).createItem(item);
        verify(dtoMapper).toDto(item);
    }

    @Test
    void createItem_whenAvailableNull_thenInvoke1MethodAndValidationExceptionThrown() {
        item.setAvailable(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> gatewayApi.createItem(item, 1L));

        assertEquals("Нет информации о доступности вещи!", exception.getMessage());
    }

    @Test
    void createItem_whenRequestIdNotNull_thenInvoke5Methods() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemService.createItem(any(Item.class))).thenReturn(item);

        item.setRequestId(1L);
        gatewayApi.createItem(item, 1L);

        verify(requestService).getRequest(1L);
        verify(userService).getUser(1L);
        verify(itemService).createItem(item);
        verify(dtoMapper).toDto(item);
    }

    @Test
    void getItem_withNoCommentsAndNoOwnerIsRequester_thenInvoke4Methods() {
        when(itemService.getItem(anyLong())).thenReturn(item);
        when(itemService.getCommentsByItem(anyLong())).thenReturn(null);

        gatewayApi.getItem(1L, 2L);

        verify(itemService).getItem(1L);
        verify(itemService).getCommentsByItem(1L);
        verify(dtoMapper).toItemDtoFull(item, null, null, new ArrayList<>());
    }

    @Test
    void getItem_withCommentsAndOwnerIsRequester_then6InvokeOf5Methods() {
        List<Comment> comments = List.of(new Comment());
        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        when(itemService.getItem(anyLong())).thenReturn(item);
        when(itemService.getCommentsByItem(anyLong())).thenReturn(comments);
        when(bookingService.getLastOrNext(anyList(), anyString())).thenReturn(bookings);

        gatewayApi.getItem(1L, 1L);

        verify(itemService).getItem(1L);
        verify(itemService).getCommentsByItem(1L);
        verify(bookingService, times(2)).getLastOrNext(anyList(), anyString());
        verify(dtoMapper).toItemDtoFull(item, booking, booking, comments);
    }

    @Test
    void getAllByUserTest() {
        List<Comment> comments = List.of(new Comment());
        Booking booking = new Booking();
        booking.setItem(item);
        List<Booking> bookings = List.of(booking);
        when(itemService.getAllByUser(anyLong(), any())).thenReturn(List.of(item));
        when(itemService.getCommentsByItem(anyLong())).thenReturn(comments);
        when(bookingService.getLastOrNext(anyList(), anyString())).thenReturn(bookings);

        gatewayApi.getAllByUser(1L, 0, 10);

        verify(userService).getUser(1L);
        verify(itemService).getAllByUser(1L, PageRequest.of(0, 10));
        verify(bookingService, times(2)).getLastOrNext(anyList(), anyString());
        verify(dtoMapper).toItemDtoFull(item, booking, booking, comments);
    }

    @Test
    void updateItemTest() {
        ItemDto expected = new ItemDto();
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemService.getItem(anyLong())).thenReturn(item);
        when(itemService.updateItem(any(Item.class))).thenReturn(item);
        when(dtoMapper.toDto(any(Item.class))).thenReturn(expected);

        ItemDto returned = gatewayApi.updateItem(1L, item, 1L);

        verify(validator).checkOwner(item, user);
        verify(itemService).updateItem(item);
        verify(dtoMapper).toDto(item);
        assertEquals(expected, returned);
    }

    @Test
    void deleteItem_whenInvoke_thenInvoke2MethodsAndReturnMessage() {
        when(itemService.deleteItem(anyLong())).thenReturn("Delete");

        String message = gatewayApi.deleteItem(1L);

        verify(itemService).deleteItem(1L);
        assertEquals("Delete", message);
    }

    @Test
    void searchByName_whenBlankText_thenReturnEmptyListAndNoInvoke() {
        List<ItemDto> returned = gatewayApi.searchByName("", 0, 10);

        verify(itemService, never()).getAllItems(any(Pageable.class));
        verify(dtoMapper, never()).toDto(any(Item.class));
        assertEquals(new ArrayList<>(), returned);
    }

    @Test
    void searchByName_whenNoBlankText_thenInvoke2Methods() {
        when(itemService.getAllItems(any(Pageable.class))).thenReturn(List.of(item));

        gatewayApi.searchByName("Item", 0, 10);

        verify(itemService).getAllItems(any(Pageable.class));
        verify(dtoMapper).toDto(item);
    }

    @Test
    void createBooking_whenInvoke_invoke6Methods() {
        bookingDto.setItemId(1L);
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemService.getItem(anyLong())).thenReturn(item);
        when(dtoMapper.toEntity(any(BookingDto.class))).thenReturn(booking);
        when(bookingService.createBooking(booking)).thenReturn(booking);

        gatewayApi.createBooking(bookingDto, 1L);

        verify(userService).getUser(1L);
        verify(itemService).getItem(1L);
        verify(dtoMapper).toEntity(bookingDto);
        verify(validator).checkBooking(booking);
        verify(bookingService).createBooking(booking);
        verify(dtoMapper).toDto(booking);
    }

    @Test
    void getBooking_whenUserIsOwner_returnBookingDto() {
        booking.setItem(item);
        booking.setBooker(new User());
        when(bookingService.getBooking(anyLong())).thenReturn(booking);
        when(dtoMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto returned = gatewayApi.getBooking(1L, 1L);

        verify(bookingService).getBooking(1L);
        verify(dtoMapper).toDto(booking);
        assertEquals(bookingDto, returned);
    }

    @Test
    void getBooking_whenUserIsBooker_returnBookingDto() {
        User user2 = new User();
        user2.setId(10L);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(user2);
        booking.setItem(item2);
        booking.setBooker(user);

        when(bookingService.getBooking(anyLong())).thenReturn(booking);
        when(dtoMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto returned = gatewayApi.getBooking(1L, 1L);

        verify(bookingService).getBooking(1L);
        verify(dtoMapper).toDto(booking);
        assertEquals(bookingDto, returned);
    }

    @Test
    void getBooking_whenUserNoBookerOrOwner_noDataFoundExceptionThrown() {
        User user2 = new User();
        user2.setId(10L);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(user2);
        booking.setItem(item2);
        booking.setBooker(new User());

        when(bookingService.getBooking(anyLong())).thenReturn(booking);


        NoDataFoundException exception = assertThrows(NoDataFoundException.class,
                () -> gatewayApi.getBooking(1L, 1L));

        verify(bookingService).getBooking(1L);
        verify(dtoMapper, never()).toDto(booking);
        assertEquals("Запрос на просмотр бронирования доступен только " +
                "для владельца брони или владельца вещи", exception.getMessage());
    }

    @Test
    void setStatus_whenOwnerAndApprovedTrue_thenStatusIsApproved() {
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        when(bookingService.getBooking(anyLong())).thenReturn(booking);

        gatewayApi.setStatus(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void setStatus_whenOwnerAndApprovedFalse_thenStatusIsRejected() {
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        when(bookingService.getBooking(anyLong())).thenReturn(booking);

        gatewayApi.setStatus(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void setStatus_whenNoOwner_noDataFoundExceptionThrown() {
        user.setId(2L);
        booking.setItem(item);
        when(bookingService.getBooking(anyLong())).thenReturn(booking);

        NoDataFoundException exception = assertThrows(NoDataFoundException.class,
                () -> gatewayApi.setStatus(1L, 1L, false));

        assertEquals("Запрос на изменение статуса бронирования доступен только " +
                "для владельца вещи", exception.getMessage());
    }

    @Test
    void setStatus_whenStatusAlreadyApproved_validationExceptionThrown() {
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.getBooking(anyLong())).thenReturn(booking);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> gatewayApi.setStatus(1L, 1L, false));

        assertEquals("Бронирование уже одобрено", exception.getMessage());
    }

    @Test
    void getAllBookingByUser_whenValidStatus_thenInvokeMethod6Times() {
        when(userService.getUser(anyLong())).thenReturn(user);

        gatewayApi.getAllBookingByUser(1L, "ALL", 0, 10);
        gatewayApi.getAllBookingByUser(1L, "CURRENT", 0, 10);
        gatewayApi.getAllBookingByUser(1L, "PAST", 0, 10);
        gatewayApi.getAllBookingByUser(1L, "FUTURE", 0, 10);
        gatewayApi.getAllBookingByUser(1L, "WAITING", 0, 10);
        gatewayApi.getAllBookingByUser(1L, "REJECTED", 0, 10);

        verify(bookingService, times(6)).getAllBookingByUser(anyLong(), any(BookingState.class), any(Pageable.class));
    }

    @Test
    void getAllBookingByUser_whenNoValidStatus_illegalArgumentExceptionThrown() {
        when(userService.getUser(anyLong())).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gatewayApi.getAllBookingByUser(1L, "UNKNOWN", 0, 10));

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void getAllBookingByOwner_whenValidStatus_thenInvokeMethod6Times() {
        when(userService.getUser(anyLong())).thenReturn(user);

        gatewayApi.getAllBookingByOwner(1L, "ALL", 0, 10);
        gatewayApi.getAllBookingByOwner(1L, "CURRENT", 0, 10);
        gatewayApi.getAllBookingByOwner(1L, "PAST", 0, 10);
        gatewayApi.getAllBookingByOwner(1L, "FUTURE", 0, 10);
        gatewayApi.getAllBookingByOwner(1L, "WAITING", 0, 10);
        gatewayApi.getAllBookingByOwner(1L, "REJECTED", 0, 10);

        verify(bookingService, times(6)).getAllBookingByOwner(anyLong(), any(BookingState.class), any(Pageable.class));
    }

    @Test
    void getAllBookingByOwner_whenNoValidStatus_illegalArgumentExceptionThrown() {
        when(userService.getUser(anyLong())).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gatewayApi.getAllBookingByOwner(1L, "UNKNOWN", 0, 10));

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void createComment_whenUserIsBookerAndValidBookingEnd_thenReturnCommentDto() {
        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setEnd(LocalDateTime.now().minusMinutes(10L));
        when(bookingService.getBookingByUserAndItem(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(itemService.createComment(any(Comment.class))).thenReturn(comment);
        when(dtoMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto returned = gatewayApi.createComment(1L, 1L, comment);

        assertEquals(commentDto, returned);
    }

    @Test
    void createComment_whenUserNotBooker_validationExceptionThrown() {
        when(bookingService.getBookingByUserAndItem(anyLong(), anyLong())).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> gatewayApi.createComment(1L, 1L, new Comment()));

        assertEquals("Пользователь с id=1 не брал вещь с id=1", exception.getMessage());
    }

    @Test
    void createComment_whenNoValidBookingEnd_validationExceptionThrown() {
        booking.setEnd(LocalDateTime.now().plusDays(1));
        when(bookingService.getBookingByUserAndItem(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> gatewayApi.createComment(1L, 1L, new Comment()));

        assertEquals("Отзыв можно оставить только после окончания аренды!", exception.getMessage());
    }

    @Test
    void createRequest_always_invoke3Methods() {
        ItemRequestDto expected = new ItemRequestDto();
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestService.createRequest(any(ItemRequest.class))).thenReturn(itemRequest);
        when(dtoMapper.toDto(any(ItemRequest.class), anyList())).thenReturn(expected);

        gatewayApi.createRequest(1L, itemRequest);

        verify(userService).getUser(1L);
        verify(requestService).createRequest(itemRequest);
        verify(dtoMapper).toDto(itemRequest, new ArrayList<>());
    }

    @Test
    void getRequestsByUser_always_thenInvoke4Methods() {
        item.setRequestId(1L);
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestService.getRequestsByUser(anyLong())).thenReturn(List.of(itemRequest));
        when(itemService.getAllByRequestsId(anyList())).thenReturn(List.of(item));
        when(dtoMapper.toDto(any(ItemRequest.class), anyList())).thenReturn(new ItemRequestDto());

        gatewayApi.getRequestsByUser(1L);

        verify(userService).getUser(1L);
        verify(requestService).getRequestsByUser(1L);
        verify(itemService).getAllByRequestsId(List.of(1L));
        verify(dtoMapper).toDto(itemRequest, List.of(item));
    }

    @Test
    void getRequestsByParam_always_thenInvoke3Methods() {
        item.setRequestId(1L);
        when(requestService.getRequestsByParam(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequest));
        when(itemService.getAllByRequestsId(anyList())).thenReturn(List.of(item));
        when(dtoMapper.toDto(any(ItemRequest.class), anyList())).thenReturn(new ItemRequestDto());

        gatewayApi.getRequestsByParam(1L, 0, 10);

        verify(requestService).getRequestsByParam(1L, 0, 10);
        verify(itemService).getAllByRequestsId(List.of(1L));
        verify(dtoMapper).toDto(itemRequest, List.of(item));
    }

    @Test
    void getRequest() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestService.getRequest(anyLong())).thenReturn(itemRequest);
        when(itemService.getAllByRequestsId(anyList())).thenReturn(List.of(item));
        when(dtoMapper.toDto(any(ItemRequest.class), anyList())).thenReturn(new ItemRequestDto());

        gatewayApi.getRequest(1L, 1L);

        verify(userService).getUser(1L);
        verify(requestService).getRequest(1L);
        verify(itemService).getAllByRequestsId(List.of(1L));
        verify(dtoMapper).toDto(itemRequest, List.of(item));
    }
}
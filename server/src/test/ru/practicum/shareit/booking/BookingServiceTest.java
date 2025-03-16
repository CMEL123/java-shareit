package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingService bookingService;

    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long itemId = 1L;
    private final Long bookingId = 1L;
    private final User user = new User(userId, "User", "user@mail.com");
    private final User owner = new User(ownerId, "Owner", "owner@mail.com");
    private final Item item = new Item(itemId, "Item", "Description", true, owner, null);
    private final LocalDateTime start = LocalDateTime.now().plusHours(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(1);

    @Test
    void testCreateValidBooking() {
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(itemId);
        request.setStart(start);
        request.setEnd(end);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.createBooking(request, userId);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
        verify(bookingRepository).save(any());
    }

    @Test
    void TestCreateBookingWithInvalidUserShouldThrow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(new BookingRequestDto(), userId));
    }

    @Test
    void testApproveBookingByOwner() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Booking booking = new Booking(bookingId, start, end, item, user, Status.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(bookingId, true, ownerId);

        assertEquals(Status.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void testApproveBookingByNonOwnerShouldThrow() {
        Booking booking = new Booking(bookingId, start, end, item, user, Status.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessException.class,
                () -> bookingService.approveBooking(bookingId, true, userId));
    }

    @Test
    void testGetBookingByIdForAuthor() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Booking booking = new Booking(bookingId, start, end, item, user, Status.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findById(bookingId, userId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
    }


    @Test
    void testFindAllBookingsWithStateFuture() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(anyLong(), any()))
                .thenReturn(List.of(new Booking()));

        List<BookingDto> result = bookingService.findAll(BookingState.FUTURE, userId);

        assertFalse(result.isEmpty());
        verify(bookingRepository).findByBookerIdAndStartDateAfterOrderByStartDateDesc(anyLong(), any());
    }

    @Test
    void testValidateBookingDates() {
        BookingRequestDto invalidRequest = new BookingRequestDto();
        invalidRequest.setItemId(itemId);
        invalidRequest.setStart(LocalDateTime.now().minusDays(1));
        invalidRequest.setEnd(LocalDateTime.now().plusHours(1));


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(invalidRequest, userId));
    }

    @Test
    void testCheckBookingItemAvailability() {
        Item unavailableItem = new Item(itemId, "Item", "Desc", false, owner, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(unavailableItem));

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(itemId);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingRequestDto, userId));
    }

    @Test
    void testFindAllOwnerBookingsWithStateCurrent() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(new Booking()));

        List<BookingDto> result = bookingService.findAllOwner(BookingState.CURRENT, ownerId);

        assertFalse(result.isEmpty());
        verify(bookingRepository).findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                anyLong(), any(), any());
    }
}


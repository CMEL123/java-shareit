package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private User user;
    private Item item;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    public void setUp() {
        user = new User(userId, "User", "user@mail.ru");
        item = new Item(itemId, "Item", "Description", true, user, null);
        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Test comment");
    }

    @Test
    void testFindByUserId() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.findByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(itemId, result.getFirst().getId());
        verify(itemRepository).findByOwnerId(userId);
    }

    @Test
    void testFindByIdWhenUserIsOwner() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndEndDateBeforeAndStatusOrderByEndDateDesc(any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findByItemIdAndEndDateAfterAndStatusOrderByEndDate(any(), any(), any()))
                .thenReturn(List.of());
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of());

        ItemWithBookingsCommentsDto result = itemService.findById(itemId, userId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        verify(itemRepository).findById(itemId);
    }

    @Test
    void testFindByTextWithBlankText() {
        List<ItemDto> result = itemService.findByText("  ");

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateItemWithValidation() {
        ItemResponseDto newItemDto = new ItemResponseDto();
        newItemDto.setName("New");
        newItemDto.setDescription("Desc");
        newItemDto.setAvailable(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.create(newItemDto, userId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        verify(itemRepository).save(any());
    }

    @Test
    void testCreateItemWithoutNameShouldThrow() {
        ItemResponseDto invalidItem = new ItemResponseDto();
        invalidItem.setName(" ");
        invalidItem.setDescription("Desc");
        assertThrows(ValidationException.class,
                () -> itemService.create(invalidItem, userId));
    }

    @Test
    void testUpdateItemWithPartialData() {
        ItemResponseDto updateDto = new ItemResponseDto();
        updateDto.setName("Updated");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.update(updateDto, itemId, userId);

        assertEquals("Updated", result.getName());
        assertEquals("Description", result.getDescription());
    }

    @Test
    void testCreateCommentWithoutBookingShouldThrow() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndDateBefore(any(), any(), any(), any()))
                .thenReturn(List.of());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentCreateDto, userId, itemId));
    }

    @Test
    void testGetNotExistingUserShouldThrow() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.checkUser(999L));
    }

    @Test
    void testGetItemsByRequest() {
        when(itemRepository.findByRequestId(any())).thenReturn(List.of(item));

        List<Item> result = itemService.getItemsByRequest(1L);

        assertEquals(1, result.size());
        assertEquals(itemId, result.getFirst().getId());
    }

    @Test
    void testCheckItemRequestValidation() {
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.checkItemRequest(999L));
    }
}


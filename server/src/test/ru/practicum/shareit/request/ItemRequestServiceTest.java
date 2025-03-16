package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
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
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestCreateDto createDto;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user", "user@mail.ru");
        itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), user);
        createDto = new ItemRequestCreateDto("description");
        item = new Item(1L, "item", "description", true, user, itemRequest);
    }

    @Test
    void testCreate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.create(createDto, 1L);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void testCreateNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(createDto, 1L));
    }

    @Test
    void testFindByUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestIdIsIn(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestWithItemsDto> result = itemRequestService.findByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals(1, result.getFirst().getItems().size());
    }

    @Test
    void testFindById() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestWithItemsDto result = itemRequestService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void testFindByIdNotFound() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(1L));
    }

    @Test
    void testFindAll() {
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.findAll(2L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

    @Test
    void testCheckUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.checkUser(1L));
    }
}
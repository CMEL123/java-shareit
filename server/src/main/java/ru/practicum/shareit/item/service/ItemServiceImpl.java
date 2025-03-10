package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.comment.CommentMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDto> findByUserId(Long userId) {
        checkUser(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemWithBookingsCommentsDto findById(Long itemId, Long userId) {

        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isEmpty()) {
            throw new NotFoundException(String.format("Вещь с id = %d не найден", itemId));
        }

        Optional<Booking> lastBooking = Optional.empty();
        Optional<Booking> nextBooking = Optional.empty();

        //Нигде постановки на это нет, в пачке в одной из бесед было написано так:
        //  в lastbooking пишется последняя бронь вещи, но ее должен видеть только владелец,
        //  а не тот кто брал вещь, поэтому логично, что null
        if (Objects.equals(userId, item.get().getOwner().getId())) {
            lastBooking = bookingRepository.findByItemIdAndEndDateBeforeAndStatusOrderByEndDateDesc(
                    itemId, LocalDateTime.now(), Status.APPROVED).stream().findFirst();
            nextBooking = bookingRepository.findByItemIdAndEndDateAfterAndStatusOrderByEndDate(
                    itemId, LocalDateTime.now(), Status.APPROVED).stream().findFirst();
        }

        List<Comment> comments = commentRepository.findByItemId(itemId);

        return ItemMapper.toItemWithBookingsCommentsDto(item.get(), lastBooking, nextBooking, comments);

    }

    @Override
    public List<ItemDto> findByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Map<Long, List<Item>> getItemsWithRequest() {
        return itemRepository.findByRequestIsNotNull()
                .stream()
                .collect(Collectors.groupingBy(el -> el.getRequest().getId()));
    }

    @Override
    public List<Item> getItemsByRequest(Long requestId) {
        return itemRepository.findByRequestId(requestId);
    }

    @Override
    @Transactional
    public ItemDto create(ItemResponseDto newItemDto, Long userId) {
        Item newItem = ItemMapper.toItem(newItemDto);
        checkNewItem(newItem);
        newItem.setOwner(checkUser(userId));
        if (newItemDto.getRequestId() != null) {
            newItem.setRequest(checkItemRequest(newItemDto.getRequestId()));
        }
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    @Transactional
    public ItemDto update(ItemResponseDto newItemDto, Long id, Long userId) {
        Item newItem = ItemMapper.toItem(newItemDto);
        Item oldItem = checkItem(id);
        oldItem.setOwner(checkUser(userId));

        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentCreateDto commentCreateDto, Long userId, Long itemId) {
        User commentator = checkUser(userId);
        Item item = checkItem(itemId);

        Optional<Booking> booking = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndDateBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).stream().findAny();
        if (booking.isEmpty()) {
            throw new  ValidationException(
                    "Отзыв может " +
                    "оставить только тот пользователь, который брал эту вещь в аренду, и только после " +
                    "окончания срока аренды.");
        }

        Comment comment = CommentMapper.toCommentFromCreate(commentCreateDto, commentator, item);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User checkUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if  (user.isPresent()) {
            log.info("Пользователь c id = {} найден", userId);
            return user.get();
        }
        log.warn("Пользователь с id = {} не найден", userId);
        throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
    }

    private Item checkItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if  (item.isPresent()) {
            log.info("Вещь c id = {} найдена", itemId);
            return item.get();
        }
        log.warn("Вещь с id = {} не найдена", itemId);
        throw new NotFoundException(String.format("Вещь с id=%d не найдена", itemId));
    }

    private ItemRequest checkItemRequest(Long itemRequestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);
        if  (itemRequest.isPresent()) {
            log.info("Запрос вещи c id = {} найдена", itemRequestId);
            return itemRequest.get();
        }
        log.warn("Запрос вещи с id = {} не найдена", itemRequestId);
        throw new NotFoundException(String.format("Запрос вещи с id=%d не найдена", itemRequestId));
    }

    private void checkNewItem(Item item) {

        if (item.getName() == null || item.getName().isBlank()) {
            log.error(item.toString());
            throw new ValidationException("Имя должно быть указано");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.error(item.toString());
            throw new ValidationException("Описание должно быть указано");
        }

        if (item.getAvailable() == null) {
            log.error(item.toString());
            throw new ValidationException("Доступность должна быть указано");
        }
    }
}
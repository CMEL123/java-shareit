package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    @Transactional
    public ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, Long requestorId) {
        User requestor  = checkUser(requestorId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequestFromCreateDto(itemRequestCreateDto, requestor);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));

    }

    public List<ItemRequestWithItemsDto> findByUserId(Long requestorId) {
        checkUser(requestorId);
        Map<Long, List<Item>> hashMap = itemService.getItemsWithRequest();
        List<ItemRequestWithItemsDto> itemRequestDtos = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId)
                .stream()
                .map(el -> ItemRequestMapper.toItemRequestWithItemsDto(el, hashMap.get(el.getId())))
                .toList();
        log.info("Получено {} запросов пользователя c id = {}", itemRequestDtos.size(), requestorId);
        return itemRequestDtos;
    }

    public ItemRequestWithItemsDto findById(Long requestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isPresent()) {
            List<Item> items = itemService.getItemsByRequest(requestId);
            log.info("Запрос вещи c id = {} найден", requestId);
            return ItemRequestMapper.toItemRequestWithItemsDto(itemRequest.get(), items);
        }
        log.warn("Запрос вещи с id = {} не найден", requestId);
        throw new NotFoundException(String.format("Запрос вещи с id=%d не найден", requestId));

    }

    public List<ItemRequestDto> findAll(Long requestId) {
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
        log.info("Получено {} запросов вещей", itemRequestDtos.size());
        return itemRequestDtos;
    }

    User checkUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if  (user.isPresent()) {
            log.info("Пользователь c id = {} найден", userId);
            return user.get();
        }
        log.warn("Пользователь с id = {} не найден", userId);
        throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
    }

    ItemRequest checkItemRequest(Long itemRequestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);
        if (itemRequest.isPresent()) {
            log.info("Запрос вещи c id = {} найдено", itemRequestId);
            return itemRequest.get();
        }
        log.warn("Запрос вещи с id = {} не найдено", itemRequestId);
        throw new NotFoundException(String.format("Запрос вещи с id=%d не найдено", itemRequestId));
    }
}
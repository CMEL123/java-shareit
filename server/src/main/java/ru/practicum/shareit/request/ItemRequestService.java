package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, Long requestorId) {
        User requestor  = checkUser(requestorId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequestFromCreateDto(itemRequestCreateDto, requestor);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));

    }

    public List<ItemRequestWithItemsDto> findByUserId(Long requestorId) {
        checkUser(requestorId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
        if (itemRequests.isEmpty()) {
            log.info("Запросы пользователя c id = {} не найдены", requestorId);
            return List.of();
        }

        List<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();
        Map<Long, List<Item>> hashMap = itemRepository.findByRequestIdIsIn(itemRequestIds)
                .stream()
                .collect(Collectors.groupingBy(el -> el.getRequest().getId()));
        List<ItemRequestWithItemsDto> itemRequestDtos = itemRequests
                .stream()
                .map(el -> ItemRequestMapper.toItemRequestWithItemsDto(el, hashMap.get(el.getId())))
                .toList();
        log.info("Получено {} запросов пользователя c id = {}", itemRequestDtos.size(), requestorId);
        return itemRequestDtos;
    }

    public ItemRequestWithItemsDto findById(Long requestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isPresent()) {
            List<Item> items = itemRepository.findByRequestId(requestId);
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

}
package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findByUserId(Long userId);

    ItemDto findById(Long id);

    List<ItemDto> findByText(String text);

    ItemDto create(ItemDto item, Long userId);

    ItemDto update(ItemDto item, Long id, Long userId);
}

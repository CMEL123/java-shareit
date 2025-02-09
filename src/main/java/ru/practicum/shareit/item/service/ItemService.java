package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> findByUserId(Long userId);

    ItemDto findById(Long id);

    List<ItemDto> findByText(String text);

    ItemDto create(Item item, Long userId);

    ItemDto update(Item item, Long id, Long userId);
}

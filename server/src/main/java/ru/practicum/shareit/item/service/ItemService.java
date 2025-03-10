package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsCommentsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    List<ItemDto> findByUserId(Long userId);

    ItemWithBookingsCommentsDto findById(Long id, Long userId);

    List<ItemDto> findByText(String text);

    ItemDto create(ItemResponseDto item, Long userId);

    ItemDto update(ItemResponseDto item, Long id, Long userId);

    CommentDto createComment(CommentCreateDto commentCreateDto, Long userId, Long itemId);

    Map<Long, List<Item>> getItemsWithRequest();

    List<Item> getItemsByRequest(Long requestId);
}

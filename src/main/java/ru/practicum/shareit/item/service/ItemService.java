package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsCommentsDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findByUserId(Long userId);

    ItemWithBookingsCommentsDto findById(Long id, Long userId);

    List<ItemDto> findByText(String text);

    ItemDto create(ItemDto item, Long userId);

    ItemDto update(ItemDto item, Long id, Long userId);

    CommentDto createComment(CommentCreateDto commentCreateDto, Long userId, Long itemId);
}

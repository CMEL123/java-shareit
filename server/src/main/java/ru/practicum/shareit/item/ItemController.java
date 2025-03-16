package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping()
    public List<ItemDto> findByUserId(@RequestHeader(value = userIdHeader) Long userId) {
        return itemService.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public ItemWithBookingsCommentsDto findById(@PathVariable Long id,
                                                @RequestHeader(value = userIdHeader) Long userId) {
        return itemService.findById(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam(required = false) String text) {
        return itemService.findByText(text);
    }

    @PostMapping()
    public ItemDto create(@Valid @RequestBody ItemResponseDto item,
                          @RequestHeader(value = userIdHeader) Long userId) {
        return itemService.create(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Valid @RequestBody ItemResponseDto item,
                          @RequestHeader(value = userIdHeader) Long userId,
                          @PathVariable Long id) {
        return itemService.update(item, id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentCreateDto comment,
                                    @RequestHeader(value = userIdHeader, required = false) Long userId,
                                    @PathVariable Long itemId) {
        return itemService.createComment(comment, userId, itemId);
    }
}


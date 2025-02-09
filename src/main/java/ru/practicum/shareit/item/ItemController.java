package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping()
    public List<ItemDto> findByUserId(@RequestHeader(value = userIdHeader, required = false) Long userId) {
        checkUser(userId);
        return itemService.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam(required = false) String text) {
        return itemService.findByText(text);
    }

    @PostMapping()
    public ItemDto create(@Valid @RequestBody Item item,
                          @RequestHeader(value = userIdHeader, required = false) Long userId) {
        checkUser(userId);
        return itemService.create(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Valid @RequestBody Item item,
                          @RequestHeader(value = userIdHeader, required = false) Long userId,
                          @PathVariable Long id) {
        checkUser(userId);
        return itemService.update(item, id, userId);
    }

    private void checkUser(Long paramsUserId){
        if (paramsUserId == null) {
            throw new ValidationException("Id пользователя владельца должен быть указан");
        }
    }
}


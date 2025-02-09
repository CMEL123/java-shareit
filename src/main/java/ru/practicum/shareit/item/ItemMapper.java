package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getOwner() != null ? item.getOwner().getId() : null,
            item.getRequest() != null ? item.getRequest().getId() : null
        );
    }
}

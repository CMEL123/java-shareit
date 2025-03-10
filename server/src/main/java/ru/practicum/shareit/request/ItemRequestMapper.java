package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        dto.setRequestorId(itemRequest.getRequestor().getId());

        return dto;
    }

    public static ItemRequestWithItemsDto toItemRequestWithItemsDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        dto.setRequestorId(itemRequest.getRequestor().getId());
        if (items != null) {
            dto.setItems(items.stream()
                    .map(ItemMapper::toItemResponseDto)
                    .toList()
            );
        }

        return dto;
    }

    public static ItemRequest toItemRequestFromCreateDto(ItemRequestCreateDto dto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }
}
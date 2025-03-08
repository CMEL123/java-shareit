package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestDto create(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                            @RequestHeader(value = userIdHeader) Long userId) {
        return itemRequestService.create(itemRequestCreateDto, userId);
    }

    @GetMapping()
    public List<ItemRequestWithItemsDto> findByUserId(@RequestHeader(value = userIdHeader) Long userId) {
        return itemRequestService.findByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader(value = userIdHeader) Long userId) {
        return itemRequestService.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto findById(@PathVariable Long requestId) {
        return itemRequestService.findById(requestId);
    }
}
package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userService) {
        this.itemRepository = itemRepository;
        this.userRepository = userService;
    }

    @Override
    public List<ItemDto> findByUserId(Long userId) {
        userRepository.findById(userId);
        return itemRepository.findByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto findById(Long id) {
        return ItemMapper.toItemDto(itemRepository.findById(id));
    }

    @Override
    public List<ItemDto> findByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.findByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto create(ItemDto newItemDto, Long userId) {
        Item newItem = ItemMapper.toItem(newItemDto);
        newItem.setOwner(userRepository.findById(userId));
        return ItemMapper.toItemDto(itemRepository.create(newItem));
    }

    @Override
    public ItemDto update(ItemDto newItemDto, Long id, Long userId) {
        Item newItem = ItemMapper.toItem(newItemDto);
        newItem.setId(id);
        newItem.setOwner(userRepository.findById(userId));
        return ItemMapper.toItemDto(itemRepository.update(newItem));
    }
}


package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
@Component
public class ItemRepository {
    protected final Map<Long, Item> items = new HashMap<>();

    public Collection<Item> findByUserId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .toList();
    }

    public Item findById(Long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Вещь с id = " + id + " не найден");
        }
        return item;
    }

    public Collection<Item> findByText(String text) {
        return items.values()
                .stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    public Item create(Item item) {
        checkItem(item);
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item newItem) {
        findById(newItem.getId());
        Item oldItem = items.get(newItem.getId());
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return oldItem;
    }

    private long getNextId() {
        long currentMaxId = items.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private void checkItem(Item item) {

        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Имя должно быть указано");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание должно быть указано");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Доступность должна быть указано");
        }
    }

}

package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Getter
public class UserRepository {
    protected final Map<Long, User> users = new HashMap<>();

    public List<User> findAll() {
        return users.values().stream().toList();
    }

    public User findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }

    public User create(User user) {
        checkEmail(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        User oldUser = findById(newUser.getId());
        if (newUser.getEmail() != null) {
            checkEmail(newUser);
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        return oldUser;
    }

    public void delete(Long id) {
        findById(id);
        users.remove(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private void checkEmail(User currUser){

        if (currUser.getEmail() == null) {
            throw new ValidationException("email не может быть пустым");
        }

        for (User user : users.values()) {
            if (currUser.getEmail().equals(user.getEmail())) {
                throw new DuplicatedDataException("Этот email уже используется");
            }
        }
    }
}

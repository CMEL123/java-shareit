package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody User user, @PathVariable Long id) {
        user.setId(id);
        return userService.update(user);
    }

    @PostMapping()
    public UserDto create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}

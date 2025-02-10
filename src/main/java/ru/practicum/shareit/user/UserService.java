package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public UserDto findById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id));
    }

    public UserDto create(UserDto userDto) {
        log.info("Добавление пользователя");
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.create(user));
    }

    public UserDto update(UserDto userDto, Long id) {
        log.info("Изменение пользователя");
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        return UserMapper.toUserDto(userRepository.update(user));
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }
}

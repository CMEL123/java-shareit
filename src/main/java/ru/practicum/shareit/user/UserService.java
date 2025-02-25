package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        List<UserDto> userDtos = userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
        log.info("Получено {} пользователей", userDtos.size());
        return userDtos;
    }

    public UserDto findById(Long id) {
        Optional<User> user =  userRepository.findById(id);
        if  (user.isPresent()){
            log.info("Пользователь c id = {} найден", id);
            return UserMapper.toUserDto(user.get());
        }
        log.warn("Пользователь с id = {} не найден", id);
        throw new NotFoundException(String.format("Пользователь с id=%d не найден", id));
    }

    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("Добавление пользователя");
        User user = UserMapper.toUser(userDto);
        checkEmail(user);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        log.info("Изменение пользователя");
        User newUser = UserMapper.toUser(userDto);
        newUser.setId(id);
        User oldUser = UserMapper.toUser(findById(newUser.getId()));
        if (newUser.getEmail() != null) {
            checkEmail(newUser);
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Transactional
    public void delete(Long id) {
        UserDto userDto = findById(id);
        userRepository.delete(UserMapper.toUser(userDto));
        log.info("Пользователь с id = {}  - удален", id);
    }

    private void checkEmail(User currUser) {

        if (currUser.getEmail() == null) {
            throw new ValidationException("email не может быть пустым");
        }

        if (userRepository.findByEmail(currUser.getEmail()).isPresent()) {
            throw new DuplicatedDataException("Этот email уже используется");
        }
    }
}
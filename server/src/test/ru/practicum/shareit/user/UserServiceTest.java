package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = new User(null, "John Doe", "john.doe@example.com");
        userRepository.save(user);
        userDto = new UserDto();
        userDto.setName("Jane Doe2");
        userDto.setEmail("jane.doe2@example.com");
    }

    @Test
    public void testFindAll() {
        List<UserDto> users = userService.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.getFirst().getName()).isEqualTo(user.getName());
        assertThat(users.getFirst().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testFindById() {
        UserDto foundUser = userService.findById(user.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testFindByIdNotExist() {
        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    public void testCreate() {
        UserDto createdUser = userService.create(userDto);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo(userDto.getName());
        assertThat(createdUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    public void testCreateThrowDuplicatedDataException() {
        userDto.setEmail(user.getEmail());
        assertThrows(DuplicatedDataException.class, () -> userService.create(userDto));
    }

    @Test
    public void testUpdate() {
        UserDto updatedUser = userService.update(userDto, user.getId());
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo(userDto.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(userDto.getEmail());

        UserDto foundUser = userService.findById(user.getId());
        assertThat(foundUser.getName()).isEqualTo(userDto.getName());
        assertThat(foundUser.getEmail()).isEqualTo(userDto.getEmail());

    }

    @Test
    public void testUpdateNotExistUser() {
        assertThrows(NotFoundException.class, () -> userService.update(userDto, 999L));
    }

    @Test
    public void testDelete() {
        UserDto deletedUser = userService.delete(user.getId());
        assertThat(deletedUser).isNotNull();
        assertThat(deletedUser.getName()).isEqualTo(user.getName());
        assertThat(deletedUser.getEmail()).isEqualTo(user.getEmail());
        assertThrows(NotFoundException.class, () -> userService.findById(user.getId()));
    }

    @Test
    public void testDeleteNotExistUser() {
        assertThrows(NotFoundException.class, () -> userService.delete(999L));
    }
}
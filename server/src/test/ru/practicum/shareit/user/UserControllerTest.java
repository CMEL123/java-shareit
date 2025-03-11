package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(controllers = {UserController.class})
public class UserControllerTest  {
    @Autowired
    protected MockMvc mockMvc;
    @MockBean
    protected UserService userService;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(1L, "John Doe", "john.doe@example.com");
    }

    @Test
    public void testFindAll() throws Exception {
        List<UserDto> users = Arrays.asList(userDto);
        BDDMockito.given(userService.findAll()).willReturn(users);
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    public void testFindById() throws Exception {
        BDDMockito.given(userService.findById(1L)).willReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    public void testCreate() throws Exception {
        BDDMockito.given(userService.create(Mockito.any(UserDto.class))).willReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    public void testUpdate() throws Exception {
        BDDMockito.given(userService.update(Mockito.any(UserDto.class), Mockito.eq(1L)))
                .willReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    public void testDelete() throws Exception {
        BDDMockito.given(userService.delete(1L)).willReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }
}
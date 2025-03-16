package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(controllers = {ItemRequestController.class})
public class ItemRequestControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @MockBean
    protected ItemRequestService itemRequestService;

    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;
    private Long userId;

    @BeforeEach
    public void setUp() {
        userId = 2L;
        itemRequestCreateDto = new ItemRequestCreateDto("Описание");
        itemRequestDto = new ItemRequestDto(1L, "Описание", LocalDateTime.now(), userId);
        itemRequestWithItemsDto = new ItemRequestWithItemsDto(1L, "Описание", LocalDateTime.now(), userId, List.of());
    }

    @Test
    public void testCreate() throws Exception {
        BDDMockito.given(itemRequestService.create(Mockito.any(ItemRequestCreateDto.class), Mockito.eq(userId)))
                .willReturn(itemRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"" + itemRequestCreateDto.getDescription() + "\"}")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestorId").value(itemRequestDto.getRequestorId()));
    }

    @Test
    public void testFindById() throws Exception {
        BDDMockito.given(itemRequestService.findById(Mockito.eq(itemRequestDto.getId())))
                .willReturn(itemRequestWithItemsDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", itemRequestDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestorId").value(itemRequestDto.getRequestorId()));
    }

    @Test
    public void testFindByUserId() throws Exception {
        List<ItemRequestWithItemsDto> requests = Arrays.asList(itemRequestWithItemsDto);
        BDDMockito.given(itemRequestService.findByUserId(Mockito.eq(userId))).willReturn(requests);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemRequestWithItemsDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemRequestWithItemsDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requestorId").value(itemRequestWithItemsDto.getRequestorId()));
    }

    @Test
    public void testFindAll() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(itemRequestDto);
        BDDMockito.given(itemRequestService.findAll(Mockito.eq(userId))).willReturn(requests);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requestorId").value(itemRequestDto.getRequestorId()));
    }
}

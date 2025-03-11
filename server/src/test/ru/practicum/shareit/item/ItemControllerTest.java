package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final Long userId = 1L;
    private final ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null, null, List.of());
    private final ItemWithBookingsCommentsDto itemWithBookings = new ItemWithBookingsCommentsDto();

    @Test
    void testFindByUserId() throws Exception {
        when(itemService.findByUserId(anyLong()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item"));
    }

    @Test
    void testFindById() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(itemWithBookings);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testFindByText() throws Exception {
        when(itemService.findByText(anyString()))
                .thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testCreateItem() throws Exception {
        ItemResponseDto itemRequest = new ItemResponseDto(null, "New Item", "Desc", true, null, null);
        when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemResponseDto updateRequest = new ItemResponseDto(null, "Updated", "New desc", false, null, null);
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void testCreateComment() throws Exception {
        CommentCreateDto commentRequest = new CommentCreateDto();
        commentRequest.setText("Great item!");
        CommentDto commentDto = new CommentDto(1L, "Great item!", 1L, null, null, LocalDateTime.now());

        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"));
    }

}
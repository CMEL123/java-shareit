package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final Long userId = 1L;
    private BookingDto bookingDto;
    private BookingDto bookingApprovedDto;

    @BeforeEach
    public void setUp() {
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1),
                new ItemDto(1L, "Item", "Description", true, null, null, List.of()),
                new UserDto(1L, "John Doe", "john.doe@example.com"),
                Status.WAITING
        );

        bookingApprovedDto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1),
                new ItemDto(1L, "Item", "Description", true, null, null, List.of()),
                new UserDto(1L, "John Doe", "john.doe@example.com"),
                Status.APPROVED
        );

    }

    @Test
    void createBooking() throws Exception {
        BookingRequestDto request = new BookingRequestDto();
        request.setStart(LocalDateTime.now().plusHours(1));
        request.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingApprovedDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L));
    }

    @Test
    void getAllBookings() throws Exception {
        when(bookingService.findAll(any(), anyLong()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?state=WAITING")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getAllOwnerBookings() throws Exception {
        when(bookingService.findAllOwner(any(), anyLong()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?state=CURRENT")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L));
    }

    @Test
    void handleNotFoundException() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithoutUserIdHeaderShouldFail() throws Exception {
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(new BookingRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}


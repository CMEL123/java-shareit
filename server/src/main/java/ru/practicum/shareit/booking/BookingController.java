package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto createBooking(@RequestBody BookingRequestDto bookingDto,
                                    @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId,
                               @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> findAll(@RequestParam(defaultValue = "ALL") BookingState state,
                                    @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.findAll(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                              @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.findAllOwner(state, userId);
    }
}
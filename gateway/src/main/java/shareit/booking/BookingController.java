package shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shareit.booking.dto.BookItemRequestDto;
import shareit.booking.dto.BookingState;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
	private final String userIdHeader = "X-Sharer-User-Id";
	private final BookingClient bookingClient;

	@PostMapping()
	public ResponseEntity<Object> createBooking(@RequestBody BookItemRequestDto bookingRequestDto,
												@RequestHeader(value = userIdHeader) Long bookerId) {
		return bookingClient.createBooking(bookerId, bookingRequestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
												 @RequestParam Boolean approved,
												 @RequestHeader(value = userIdHeader) Long ownerId) {
		return bookingClient.approveBooking(bookingId, approved, ownerId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findById(@PathVariable Long bookingId,
											  @RequestHeader(value = userIdHeader) Long bookerOrOwnerId) {
		return bookingClient.findById(bookerOrOwnerId, bookingId);
	}

	@GetMapping()
	public ResponseEntity<Object> findAll(@RequestParam(defaultValue = "ALL") String stateParam,
													 @RequestHeader(value = userIdHeader) Long userId
										 ) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.findAll(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findAllOwner(@RequestParam(defaultValue = "ALL") String stateParam,
													@RequestHeader(value = userIdHeader) Long userId
												) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.findAllOwner(userId, state);
	}
}
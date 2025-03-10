package shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping()
    public ResponseEntity<Object> create(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                         @RequestHeader(value = userIdHeader) Long userId) {
        return requestClient.create(userId, itemRequestCreateDto);
    }

    @GetMapping()
    public ResponseEntity<Object> findByUserId(@RequestHeader(value = userIdHeader) Long userId) {
        return requestClient.findByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader(value = userIdHeader) Long userId) {
        return requestClient.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestId) {
        return requestClient.findById(requestId);
    }
}

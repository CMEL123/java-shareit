package shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shareit.comment.dto.CommentCreateDto;
import shareit.item.dto.ItemResponseDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping()
    public ResponseEntity<Object> findByUserId(@RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id,
                                                @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.findById(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam(required = false) String text,
                                             @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.findByText(text, userId);
    }

    @PostMapping()
    public ResponseEntity<Object> create(@Valid @RequestBody ItemResponseDto item,
                          @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.create(item, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemResponseDto item,
                          @RequestHeader(value = userIdHeader) Long userId,
                          @PathVariable Long id) {
        return itemClient.update(item, id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentCreateDto comment,
                                    @RequestHeader(value = userIdHeader) Long userId,
                                    @PathVariable Long itemId) {
        return itemClient.createComment(comment, userId, itemId);
    }
}


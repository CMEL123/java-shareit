package shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import shareit.client.BaseClient;
import shareit.comment.dto.CommentCreateDto;
import shareit.item.dto.ItemResponseDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findByUserId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findById(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> findByText(String text, Long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> create(ItemResponseDto item, Long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> update(ItemResponseDto item, Long userId, Long id) {
        return patch("/" + id, userId, item);
    }

    public ResponseEntity<Object> createComment(CommentCreateDto comment, Long userId, Long id) {
        return post("/" + id + "/comment", userId, comment);
    }
}

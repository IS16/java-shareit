package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDto itemDto) {
        return post("", userId, null, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAllItems(Long userId, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size
        );

        return get("?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> searchItems(String text, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", null, params);
    }

    public ResponseEntity<Object> updateItem(Long id, Long userId, ItemDto itemDto) {
        return patch("/" + id, userId,  null, itemDto);
    }

    public ResponseEntity<Object> deleteItem(Long id, Long userId) {
        return delete("/" + id, userId);
    }

    public ResponseEntity<Object> addComment(Long id, Long userId, CommentDto commentDto) {
        return post("/" + id + "/comment", userId, commentDto);
    }
}

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
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> createItem(ItemDto item, long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> getItem(long itemId, long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getAllItems(long userId, int from, int size) {
        return get("?from={from}&size={size}", userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> updateItem(ItemDto item, long itemId, long userId) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> deleteItem(long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> searchByName(long userId, String text, int from, int size) {
        return get("/search?text={text}&from={from}&size={size}", userId,
                Map.of("text", text, "from", from, "size", size));
    }

    public ResponseEntity<Object> createComment(CommentDto comment, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, comment);
    }

}

package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.OnCreate;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long userId,
            @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос от пользователя с id - {} на сохранение новой вещи {}", userId, itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long userId,
            @PathVariable @Positive(message = "Id вещи должно быть положительным.") long itemId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос от пользователя с id - {} на обновление данных о вещи c itemId - {}", userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long userId,
            @PathVariable @Positive(message = "Id вещи должно быть положительным.") long itemId) {
        log.info("Поступил запрос на вывод информации о вещи.");
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long userId) {
        log.info("Поступил запрос на получение списка вещей пользователя с id - {}", userId);
        return itemClient.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long userId,
            @RequestParam String text) {
        log.info("Поступил запрос на поиск вещей по описанию {}.", text);
        return itemClient.getItemsByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long authorId,
            @PathVariable @Positive(message = "Id вещи должно быть положительным.") long itemId,
            @Valid @RequestBody CommentRequest comment) {
        log.info("Поступил запрос от пользователя {} на добавление комментария к вещи {}.", authorId, itemId);
        return itemClient.addComment(authorId, itemId, comment);
    }
}

package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.ValidationUtils;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ValidationUtils validation;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long userId,
            @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос от пользователя с id - {} на сохранение новой вещи {}", userId, itemDto);
        validation.checkUserId(userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long userId,
            @PathVariable @Positive(message = "Id вещи должно быть положительным.") long itemId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос от пользователя с id - {} на обновление данных о вещи c itemId - {}", userId, itemId);
        validation.validationForUpdateItem(userId, itemId);
        return itemService.updateItem(itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long userId,
            @PathVariable @Positive(message = "Id вещи должно быть положительным.") long itemId) {
        log.info("Поступил запрос на вывод информации о вещи.");
        validation.checkUserId(userId);
        validation.checkItemId(itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long userId) {
        log.info("Поступил запрос на получение списка вещей пользователя с id - {}", userId);
        validation.checkUserId(userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        log.info("Поступил запрос на поиск вещей по описанию {}.", text);
        if (text.isBlank()) {
            return List.of();
        }
        return itemService.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long authorId,
            @PathVariable @Positive(message = "Id вещи должно быть положительным.") long itemId,
            @Valid @RequestBody CommentRequest comment) {
        log.info("Поступил запрос от пользователя {} на добавление комментария к вещи {}.", authorId, itemId);
        validation.validationForCreateComment(authorId, itemId);
        return itemService.addComment(authorId, itemId, comment);
    }

}

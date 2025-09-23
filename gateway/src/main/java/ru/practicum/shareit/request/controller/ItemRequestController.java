package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestRequest;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient requestClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    ResponseEntity<Object> addRequest(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long requestorId,
            @RequestBody @Valid ItemRequestRequest request) {
        log.info("Поступил запрос от пользователя {} на создание нового запроса {}.", requestorId, request);
        return requestClient.addRequest(requestorId, request);
    }

    @GetMapping
    ResponseEntity<Object> getRequestsByRequestor(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long requestorId) {
        log.info("Поступил запрос от пользователя {} на получение списка своих запросов.", requestorId);
        return requestClient.getRequestsByRequestor(requestorId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getRequestsByOtherRequestor(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long requestorId) {
        log.info("Поступил запрос от пользователя {} на получение списка всех запросов.", requestorId);
        return requestClient.getRequestsByOtherRequestor(requestorId);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getRequestById(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long userId,
            @PathVariable @Positive(message = "Id запроса должно быть положительным.") long requestId) {
        log.info("Поступил запрос от пользователя {} на просмотр данных запроса {}.", userId, requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}

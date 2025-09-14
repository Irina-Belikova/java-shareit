package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.ValidationUtils;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;
    private final ValidationUtils validation;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    ItemRequestResponse addRequest(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long requestorId,
            @RequestBody @Valid ItemRequestRequest request) {
        log.info("Поступил запрос от пользователя {} на создание нового запроса {}.", requestorId, request);
        validation.checkUserId(requestorId);
        return requestService.addRequest(request, requestorId);
    }

    @GetMapping
    List<ItemRequestResponse> getRequestsByRequestor(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long requestorId) {
        log.info("Поступил запрос от пользователя {} на получение списка своих запросов.", requestorId);
        validation.checkUserId(requestorId);
        return requestService.getRequestsByRequestor(requestorId);
    }

    @GetMapping("/all")
    List<ItemRequestResponse> getRequestsByOtherRequestor(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным.") long requestorId) {
        log.info("Поступил запрос от пользователя {} на получение списка всех запросов.", requestorId);
        validation.checkUserId(requestorId);
        return requestService.getRequestsByOtherRequestor(requestorId);
    }

    @GetMapping("/{requestId}")
    ItemRequestResponse getRequestById(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long userId,
            @PathVariable @Positive(message = "Id запроса должно быть положительным.") long requestId) {
        log.info("Поступил запрос от пользователя {} на просмотр данных запроса {}.", userId, requestId);
        validation.checkUserId(userId);
        validation.checkRequestId(requestId);
        return requestService.getRequestById(requestId);
    }
}

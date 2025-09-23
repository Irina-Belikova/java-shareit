package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.validation.OnCreate;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addBooking(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long bookerId,
            @Validated(OnCreate.class) @RequestBody BookingRequest bookingRequest) {
        log.info("Поступил запрос от пользователя с id - {} на создание нового бронирования {}", bookerId, bookingRequest);
        bookingRequest.validEndTime();
        return bookingClient.addBooking(bookerId, bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long ownerId,
            @PathVariable @Positive(message = "Id бронирования должно быть положительным.") long bookingId,
            @RequestParam @NotNull(message = "Параметр approved обязателен.") Boolean approved) {
        log.info("Поступил запрос от пользователя {} на изменение статуса бронирования.", ownerId);
        return bookingClient.updateBookingStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long userId,
            @PathVariable @Positive(message = "Id бронирования должно быть положительным.") long bookingId) {
        log.info("Поступил запрос от пользователя {} на получение данных о бронировании {}.", userId, bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerId(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long bookerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Поступил запрос от пользователя {} на получение списка бронирований в статусе {}", bookerId, state);
        return bookingClient.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerItems(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Получен запрос от пользователя {} на получение списка бронирований его вещей.", ownerId);
        return bookingClient.getBookingsByOwnerItems(ownerId, state);
    }
}

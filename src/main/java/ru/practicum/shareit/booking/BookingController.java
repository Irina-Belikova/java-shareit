package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.ValidationUtils;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final ValidationUtils validation;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponse addBooking(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long bookerId,
            @Validated(OnCreate.class) @RequestBody BookingRequest bookingRequest) {
        log.info("Поступил запрос от пользователя с id - {} на создание нового бронирования {}", bookerId, bookingRequest);
        validation.validationForCreateBooking(bookingRequest, bookerId);
        return bookingService.addBooking(bookingRequest, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateBookingStatus(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long ownerId,
            @PathVariable @Positive(message = "Id бронирования должно быть положительным.") long bookingId,
            @RequestParam @NotNull(message = "Параметр approved обязателен.") Boolean approved) {
        log.info("Поступил запрос от пользователя {} на изменение статуса бронирования.", ownerId);
        validation.validationForUpdateBookingStatus(bookingId, ownerId);
        return bookingService.updateBookingStatus(bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long userId,
            @PathVariable @Positive(message = "Id бронирования должно быть положительным.") long bookingId) {
        log.info("Поступил запрос от пользователя {} на получение данных о бронировании {}.", userId, bookingId);
        validation.validationForGetBookingById(bookingId, userId);
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping
    public List<BookingResponse> getBookingsByBookerId(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long bookerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Поступил запрос от пользователя {} на получение списка бронирований в статусе {}", bookerId, state);
        validation.checkUserId(bookerId);
        return bookingService.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwnerItems(
            @RequestHeader(USER_ID)
            @Positive(message = "Id пользователя должно быть положительным") long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Получен запрос от пользователя {} на получение списка бронирований его вещей.", ownerId);
        validation.validationOwnerHasItems(ownerId);
        return bookingService.getBookingsByOwnerItems(ownerId, state);
    }
}

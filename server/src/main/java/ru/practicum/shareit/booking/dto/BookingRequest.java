package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequest {

    /**
     * Дата и время начала бронирования.
     */
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    private LocalDateTime end;

    /**
     * id вещи, которую пользователь бронирует.
     */
    private Long itemId;

    /**
     * Id пользователя (зарегистрированный), который осуществляет бронирование.
     */
    private Long bookerId;

    /**
     * Статус бронирования.
     */
    private BookingStatus status;
}

package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 * DTO для взаимодействия с контроллером
 */
public class BookingDto {
    /**
     * Уникальный идентификатор бронирования.
     * Генерируется программой.
     * В десериализации JSON-запроса не участвует(JsonProperty.Access.READ_ONLY)
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /**
     * Дата и время начала бронирования.
     */
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    private LocalDateTime end;

    /**
     * Вещь, которую пользователь бронирует.
     */
    private Item item;

    /**
     * Id пользователя (зарегистрированный), который осуществляет бронирование.
     */
    @NotNull(message = "Данные о бронирующем должны быть указаны.")
    private Long bookerId;

    /**
     * Статус бронирования.
     */
    private BookingStatus status;
}

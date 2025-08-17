package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 * Класс для бронирования вещей.
 */
@Data
@Builder
public class Booking {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;

    /**
     * Пользователь (зарегистрированный), который осуществляет бронирование.
     */
    private User booker;

    private BookingStatus status;
}

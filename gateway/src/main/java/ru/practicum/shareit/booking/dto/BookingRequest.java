package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.validation.OnCreate;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequest {

    /**
     * Дата и время начала бронирования.
     */
    @NotNull(groups = OnCreate.class)
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом.")
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    @NotNull(groups = OnCreate.class)
    @FutureOrPresent(message = "Дата окончания бронирования не может быть в прошлом.")
    private LocalDateTime end;

    /**
     * id вещи, которую пользователь бронирует.
     */
    @NotNull(message = "id о вещи должен быть указан.")
    private Long itemId;

    /**
     * Id пользователя (зарегистрированный), который осуществляет бронирование.
     */
    @NotNull(message = "id бронирующего должен быть указан.")
    private Long bookerId;

    /**
     * Статус бронирования.
     */
    private BookingStatus status;

    public void validEndTime() {
        if (!end.isAfter(start)) {
            throw new ValidationException(String.format("Дата окончания брони %s должна быть позднее даты начала %s.",
                    end, start));
        }
    }
}

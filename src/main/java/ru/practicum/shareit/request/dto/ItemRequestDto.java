package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 * DTO для взаимодействия с контроллером
 */
public class ItemRequestDto {

    /**
     * Уникальный идентификатор запроса.
     * Генерируется программой.
     * В десериализации JSON-запроса не участвует(JsonProperty.Access.READ_ONLY)
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /**
     * Описание вещи. Не более 255 символов - достаточное значение для описания функциональности.
     */
    @Size(min = 1, max = 255, message = "Описание необходимой вещи должно быть в диапазоне 1 - 255 символов.")
    private String description;

    /**
     * Пользователь, создавший запрос. Не может быть пустым.
     */
    @NotNull(message = "Данные о пользователе, создающем запрос должны быть указаны.")
    private Long requestorId;

    /**
     * Дата и время создания запроса. При создании запроса создаётся автоматически.
     * Задаётся программой.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime created;
}

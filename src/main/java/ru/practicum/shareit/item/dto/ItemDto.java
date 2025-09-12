package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 * DTO для взаимодействия с контроллером.
 */
@Data
@Builder
public class ItemDto {
    /**
     * Уникальный идентификатор вещи.
     * Генерируется программой.
     * В десериализации JSON-запроса не участвует(JsonProperty.Access.READ_ONLY)
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /**
     * Название вещи.
     * Короткое имя, длиной не более 50 символов.
     */
    @NotBlank(groups = OnCreate.class, message = "Не заполнено поле название вещи.")
    @Size(min = 1, max = 50, message = "Название вещи должно быть от 1 до 50 символов.")
    private String name;

    /**
     * Описание свойств вещи.
     * Ограничение по длине описания 255 символов.
     */
    @NotBlank(groups = OnCreate.class, message = "Не заполнено поле описание вещи.")
    @Size(min = 1, max = 255, message = "Описание вещи должно быть в пределах 1 - 255 символов.")
    private String description;

    /**
     * Статус о том, доступна (true) или нет (false) вещь для аренды.
     */
    @NotNull(groups = OnCreate.class, message = "Статус вещи должен быть указан.")
    private Boolean available;

    /**
     * id запроса другого пользователя, если по нему была создана вещь.
     */
    private Long requestId;

    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentResponse> comments;
}

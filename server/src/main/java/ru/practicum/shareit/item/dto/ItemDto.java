package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String name;

    /**
     * Описание свойств вещи.
     * Ограничение по длине описания 255 символов.
     */
    private String description;

    /**
     * Статус о том, доступна (true) или нет (false) вещь для аренды.
     */
    private Boolean available;

    /**
     * id запроса другого пользователя, если по нему была создана вещь.
     */
    private Long requestId;

    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentResponse> comments;
}

package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 * Класс для хранения запросов о необходимых вещах, которых нет в базе.
 */
@Data
@Builder
public class ItemRequest {

    private Long id;
    private String description;
    /**
     * Пользователь, создавший запрос.
     */
    private User requestor;

    private LocalDateTime created;
}

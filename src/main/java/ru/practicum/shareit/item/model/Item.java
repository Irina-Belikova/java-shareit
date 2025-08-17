package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 * Класс для хранения информации о вещах, имеющихся в базе.
 */
@Data
@Builder
public class Item {

    private Long id;
    private String name;
    private String description;
    private Boolean available;

    /**
     * Владелец вещи (зарегистрированный пользователь).
     */
    private User owner;

    private ItemRequest request;
}

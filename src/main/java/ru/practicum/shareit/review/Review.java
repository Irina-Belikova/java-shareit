package ru.practicum.shareit.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Класс для написания отзывов о вещах пользователями.
 */
public class Review {

    /**
     * Уникальный идентификатор отзыва.
     * Генерируется программой.
     */
    private Long id;

    /**
     * Пользователь (зарегистрированный), осуществивший бронирование.
     * Не может быть null.
     */
    @NotNull
    private User booker;

    /**
     * Вещь, которую бронировали.
     * Не может быть пустым.
     */
    @NotNull
    private Item item;

    /**
     * Отзыв пользователя.
     * Поле не может быть пустым.
     */
    @NotBlank
    private String review;

    /**
     * Оценка успешности выполнения поставленной задачи:
     * true - вещь с задачей справилась,
     * false - вещь с задачей не справилась
     */
    private Boolean isPositive;
}

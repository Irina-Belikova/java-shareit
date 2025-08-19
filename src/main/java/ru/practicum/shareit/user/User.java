package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 * Класс для хранения информации о зарегистрированных пользователях.
 */
@Data
@Builder
public class User {

    private Long id;
    private String name;
    private String email;
}

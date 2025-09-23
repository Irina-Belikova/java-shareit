package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для взаимодействия с контроллером
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * Уникальный идентификатор пользователя
     * Генерируется программой
     * В десериализации JSON-запроса не участвует(JsonProperty.Access.READ_ONLY)
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /**
     * Имя или логин пользователя
     * Не может быть пустым
     */
    private String name;

    /**
     * Email пользователя
     * Не может быть пустым
     */
    private String email;

    /**
     * Методы для проверки заполненности полей, т.к. при обновлении данных поля могут приходить незаполненными.
     */
    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }
}

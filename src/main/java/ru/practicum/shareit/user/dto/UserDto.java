package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

/**
 * DTO для взаимодействия с контроллером
 */
@Data
@Builder
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
    @NotBlank(groups = OnCreate.class, message = "Имя пользователя не может быть пустым.")
    @Size(groups = OnCreate.class, min = 1, max = 50, message = "Имя должно быть в пределах 1 - 50 символов.")
    private String name;

    /**
     * Email пользователя
     * Не может быть пустым
     */
    @NotBlank(groups = OnCreate.class, message = "Поле email не может быть пустым.")
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "Некорректный формат email-адреса.")
    @Size(groups = OnCreate.class, min = 1, max = 50, message = "Email должен быть в пределах 1 - 50 символов.")
    private String email;

    /**
     * Методы для проверки заполненности полей, т.к. при обновлении данных поля могут приходить незаполненными.
     */
    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}

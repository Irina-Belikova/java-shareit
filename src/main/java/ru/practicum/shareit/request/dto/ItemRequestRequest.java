package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestRequest {

    @NotBlank(message = "Запрос не может быть пустым.")
    @Size(min = 1, max = 512, message = "Текст запроса описания вещи должен быть в пределах 1 - 512 символов.")
    private String description;
}

package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Комментарий не может быть пустым.")
    @Size(min = 1, max = 512, message = "Описание вещи должно быть в пределах 1 - 512 символов.")
    private String text;
}

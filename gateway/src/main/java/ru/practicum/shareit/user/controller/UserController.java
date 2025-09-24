package ru.practicum.shareit.user.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание нового пользователя {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable @Positive(message = "Id пользователя должен быть положительным.") Long userId,
            @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновления данных пользователя с id - {} на {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(
            @PathVariable @Positive(message = "Id пользователя должен быть положительным.") Long userId) {
        log.info("Получен запрос на поиск пользователя с id - {}.", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable @Positive(message = "Id пользователя должен быть положительным.") Long userId) {
        log.info("Получен запрос на удаление пользователя с id - {}.", userId);
        return userClient.deleteUserById(userId);
    }
}

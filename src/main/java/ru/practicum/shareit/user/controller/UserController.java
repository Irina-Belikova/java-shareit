package ru.practicum.shareit.user.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;
import ru.practicum.shareit.validation.ValidationUtils;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;
    private final ValidationUtils validation;

    @PostMapping
    public UserDto createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание нового пользователя {}", userDto);
        validation.validationForCreateUser(userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(
            @PathVariable @Positive(message = "Id пользователя должен быть положительным.") Long userId,
            @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновления данных пользователя с id - {} на {}", userId, userDto);
        validation.validationForUpdateUser(userId, userDto);
        return userService.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable @Positive(message = "Id пользователя должен быть положительным.") Long userId) {
        log.info("Получен запрос на поиск пользователя с id - {}.", userId);
        validation.checkUserId(userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(
            @PathVariable @Positive(message = "Id пользователя должен быть положительным.") Long userId) {
        log.info("Получен запрос на удаление пользователя с id - {}.", userId);
        validation.checkUserId(userId);
        userService.deleteUserById(userId);
        return ResponseEntity.ok("Пользователь успешно удалён.");
    }
}

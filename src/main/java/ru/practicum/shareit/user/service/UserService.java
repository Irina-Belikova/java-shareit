package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    /**
     * Добавление нового пользователя в базу
     * @param userDto - dto-объект нового пользователя
     * @return dto-объект
     */
    UserDto createUser(UserDto userDto);

    /**
     * Обновление данных о пользователе
     * @param id - id пользователя, который будет обновляться
     * @param userDto - dto-объект с обновлёнными данными
     * @return обновлённый dto-объект
     */
    UserDto updateUser(long id, UserDto userDto);

    /**
     * Получение пользователя по его id
     * @param id - id пользователя
     * @return dto-объект
     */
    UserDto getUserById(long id);

    /**
     * Удаление пользователя из базы по его id
     * @param id - id пользователя
     */
    void deleteUserById(long id);
}

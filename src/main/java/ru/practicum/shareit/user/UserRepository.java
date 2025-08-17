package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {

    /**
     * Добавление нового пользователя в базу
     * @param user - объект нового пользователя
     * @return объект User
     */
    User createUser(User user);

    /**
     * Обновление данных о пользователе
     * @param user - объект с обновлёнными данными
     * @return обновлённый объект User
     */
    User updateUser(User user);

    /**
     * Получение пользователя по его id
     * @param id - id пользователя
     * @return Optional найденного или нет пользователя
     */
    Optional<User> getUserById(Long id);

    /**
     * Удаление пользователя из базы по его id
     * @param id - id пользователя
     */
    void deleteUserById(Long id);

    /**
     * Получение пользователя по его email
     * @param email - email пользователя
     * @return Optional найденного или нет пользователя
     */
    Optional<User> getUserByEmail(String email);
}

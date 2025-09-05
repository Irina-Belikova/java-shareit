package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Получение пользователя по его email
     *
     * @param email - email пользователя
     * @return Optional найденного или нет пользователя
     */
    Optional<User> findByEmail(String email);
}

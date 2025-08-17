package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ServerErrorException;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public User createUser(User user) {
        try {
            user.setId(++userId);
            users.put(user.getId(), user);
            return user;
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка сохранения данных пользователя.");
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            users.put(user.getId(), user);
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка обновления данных пользователя.");
        }
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        try {
            return Optional.ofNullable(users.get(id));
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка при поиске пользователя.");
        }
    }

    @Override
    public void deleteUserById(Long id) {
        try {
            users.remove(id);
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка удаления данных пользователя.");
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        try {
            return users.values().stream()
                    .filter(user -> email.equals(user.getEmail()))
                    .findFirst();
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка при поиске пользователя по email.");
        }
    }
}

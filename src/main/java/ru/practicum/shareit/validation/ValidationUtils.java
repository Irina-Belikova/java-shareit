package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Класс для проверки корректности входящих параметров в контроллерах
 */
@Component
@RequiredArgsConstructor
public class ValidationUtils {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public void validationForCreateUser(UserDto userDto) {
        String email = userDto.getEmail();
        if (userRepository.getUserByEmail(email).isPresent()) {
            throw new DuplicateDataException(String.format("Такой email - %s уже существует.", email));
        }
    }

    public void validationForUpdateUser(long id, UserDto userDto) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с таким id - %s не существует.", id)));
        if (userDto.hasEmail()) {
            if (userRepository.getUserByEmail(userDto.getEmail()).isPresent()) {
                throw new DuplicateDataException(String.format("Такой email - %s уже существует.", userDto.getEmail()));
            }
        }
    }

    public void checkUserId(long id) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с таким id - %s не существует.", id)));
    }

    public void checkItemId(long id) {
        Item item = itemRepository.getItemById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещи с таким id -%s не существует.", id)));
    }

    public void validationForUpdateItem(long userId, long itemId) {
        checkUserId(userId);
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещи с таким id -%s не существует.", itemId)));
        long itemUserId = item.getOwner().getId();
        if (userId != itemUserId) {
            throw new ValidationException(String.format("Пользователь с id -%s не хозяин вещи.", userId));
        }
    }
}

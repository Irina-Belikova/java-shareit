package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * Класс для проверки корректности входящих параметров в контроллерах
 */
@Component
@RequiredArgsConstructor
public class ValidationUtils {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public void validationForCreateUser(UserDto userDto) {
        String email = userDto.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateDataException(String.format("Такой email - %s уже существует.", email));
        }
    }

    public void validationForUpdateUser(long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с таким id - %s не существует.", id)));
        if (userDto.hasEmail()) {
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new DuplicateDataException(String.format("Такой email - %s уже существует.", userDto.getEmail()));
            }
        }
    }

    public void checkUserId(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с таким id - %s не существует.", id)));
    }

    public void checkItemId(long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещи с таким id -%s не существует.", id)));
    }

    public void validationForUpdateItem(long userId, long itemId) {
        checkUserId(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещи с таким id -%s не существует.", itemId)));
        long itemUserId = item.getOwner().getId();
        if (userId != itemUserId) {
            throw new ValidationException(String.format("Пользователь с id -%s не хозяин вещи.", userId));
        }
    }

    public void validationForCreateBooking(BookingRequest bookingRequest, long bookerId) {
        checkUserId(bookerId);
        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", bookingRequest.getItemId())));

        if (!bookingRequest.validEndTime()) {
            throw new ValidationException(String.format("Дата окончания брони %s должна быть позднее даты начала %s.",
                    bookingRequest.getEnd(), bookingRequest.getStart()));
        }

        Long ownerId = item.getOwner().getId();

        if (bookerId == ownerId) {
            throw new ValidationException("Хозяин вещи не может бронировать свою собственную вещь.");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }
    }

    public void validationForUpdateBookingStatus(long bookingId, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ValidationException(String.format("Пользователя с таким id - %s не существует.", ownerId)));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с таким id - %s не найдено.", bookingId)));
        Item item = booking.getItem();
        if (ownerId != item.getOwner().getId()) {
            throw new ValidationException(String.format("Пользователь с id - %s не является хозяином вещи.", ownerId));
        }
    }

    public void validationForGetBookingById(long bookingId, long userId) {
        checkUserId(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с таким id - %s не найдено.", bookingId)));
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();

        if (bookerId != userId) {
            if (ownerId != userId) {
                throw new ValidationException(String.format("Пользователь с id - %s не является хозяином вещи или бронирующим.", userId));
            }
        }
    }

    public void validationOwnerHasItems(long ownerId) {
        checkUserId(ownerId);
        Item item = itemRepository.findFirstByOwnerId(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("У пользователя %s нет ни одной вещи.", ownerId)));
    }

    public void validationForCreateComment(long authorId, long itemId) {
        checkUserId(authorId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещи с таким id -%s не существует.", itemId)));

        if (authorId == item.getOwner().getId()) {
            throw new ValidationException("Хозяин вещи не может оставлять комментарии о ней.");
        }

        Booking booking = bookingRepository.findByBookerIdAndItemId(authorId, itemId)
                .orElseThrow(() -> new ValidationException(String.format("Данный пользователь %s не брал вещь %s в аренду",
                        authorId, itemId)));

        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Комментарии можно оставлять только на подтвержденные бронирования");
        }

        LocalDateTime time = LocalDateTime.now();
        if (time.isAfter(booking.getStart()) && time.isBefore(booking.getEnd())) {
            throw new ValidationException("Комментарии можно оставлять только за прошедшие бронирования.");
        }
    }
}

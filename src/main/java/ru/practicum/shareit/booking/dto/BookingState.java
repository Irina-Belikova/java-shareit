package ru.practicum.shareit.booking.dto;

/**
 * Состояние бронирования:
 * ALL - все
 * CURRENT - текущие
 * PAST - завершённые
 * FUTURE - будущие
 * WAITING - ожидающие подтверждения
 * REJECTED - отклоненные
 */
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}

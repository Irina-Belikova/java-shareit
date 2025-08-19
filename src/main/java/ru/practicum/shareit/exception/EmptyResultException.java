package ru.practicum.shareit.exception;

public class EmptyResultException extends RuntimeException {
    public EmptyResultException(String message) {
        super(message);
    }
}

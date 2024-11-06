package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyExistsException extends IllegalArgumentException {
    public UserAlreadyExistsException(String message) {
        super(message);
        log.error(message);
    }
}
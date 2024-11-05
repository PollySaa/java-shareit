package ru.practicum.shareit.booking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingState;

public class ValidatorBookingState implements ConstraintValidator<ValidBookingState, String> {
    @Override
    public boolean isValid(String state, ConstraintValidatorContext context) {
        return state == null || BookingState.from(state).isPresent();
    }
}

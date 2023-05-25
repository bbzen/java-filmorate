package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateFutureValidator implements ConstraintValidator<DateFuture, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date.isBefore(LocalDate.now());
    }
}

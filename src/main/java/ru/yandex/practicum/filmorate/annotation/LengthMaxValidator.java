package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LengthMaxValidator implements ConstraintValidator<LengthMax, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.length() < 200;
    }
}

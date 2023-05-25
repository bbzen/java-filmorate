package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserLoginValidator implements ConstraintValidator<UserLoginValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null || !value.isBlank() || !value.contains(" ");
    }
}

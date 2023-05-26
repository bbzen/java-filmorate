package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {DateReleaseValidator.class})
public @interface DateRelease {
    String message() default "Дата релиза должна быть не раньше 28 декабря 1895 года.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateFuture;
import ru.yandex.practicum.filmorate.annotation.UserLoginValid;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    private int id;
    @Email(message = "электронная почта не может быть пустой и должна содержать символ @")
    @NotBlank
    private final String email;
    @UserLoginValid
    private final String login;
    private String name;
    @DateFuture
    private final LocalDate birthday;
}

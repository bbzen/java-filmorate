package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class User {
private final int id;
private final String email;
private final String login;
private final String name;
private final LocalDate birthday;
}

package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
private int id;
private final String email;
private final String login;
private String name;
private final LocalDate birthday;
}

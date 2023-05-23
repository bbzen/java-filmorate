package ru.yandex.practicum.filmorate.model;

import java.time.Duration;
import java.time.LocalDate;

import lombok.Data;

@Data
public class Film {
    private final int id;
    private final String name;
    private String description;
    private final LocalDate releaseDate;
    private final Duration duration;
}

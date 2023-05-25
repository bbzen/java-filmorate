package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    private final String name;
    private String description;
    private final LocalDate releaseDate;
    private final long duration;
}

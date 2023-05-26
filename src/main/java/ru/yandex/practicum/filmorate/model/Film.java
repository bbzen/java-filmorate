package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateRelease;
import ru.yandex.practicum.filmorate.annotation.LengthMax;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank
    private final String name;
    @LengthMax
    private String description;
    @DateRelease
    private final LocalDate releaseDate;
    @Positive
    private final long duration;
}

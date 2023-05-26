package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            log.debug("Данный фильм отсутствует в базе данных.");
            throw new ValidationException("Данный фильм отсутствует в базе данных.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {


    @PostMapping
    public Film create(@RequestBody Film film) {
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
    }
}

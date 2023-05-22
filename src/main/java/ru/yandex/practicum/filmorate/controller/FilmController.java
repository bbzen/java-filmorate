package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        if (film != null && !films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        if (film != null && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}

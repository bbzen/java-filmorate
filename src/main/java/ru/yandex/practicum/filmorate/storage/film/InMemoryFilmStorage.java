package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 0;
    private final Map<Integer, Film> films;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void createFilm(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        isFilmInStorage(film.getId());
        films.put(film.getId(), film);
    }

    @Override
    public void removeFilm(Film film) {
        isFilmInStorage(film.getId());
        films.remove(film.getId());
    }

    private void isFilmInStorage(int id) {
        if (!films.containsKey(id)) {
            log.debug("Данный фильм отсутствует в базе данных.");
            throw new FilmNotFoundException("Данный фильм отсутствует в базе данных.");
        }
    }
}

package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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

    public Film findById(int id) {
        isFilmPresent(id);
        return films.get(id);
    }

    @Override
    public boolean containsFilm(int id) {
        return films.containsKey(id);
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        return null;
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        isFilmPresent(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void removeFilm(Film film) {
        isFilmPresent(film.getId());
        films.remove(film.getId());
    }

    private void isFilmPresent(int id) {
        if (!films.containsKey(id)) {
            log.debug("Данный фильм отсутствует в базе данных.");
            throw new FilmNotFoundException("Данный фильм отсутствует в базе данных.");
        }
    }
}

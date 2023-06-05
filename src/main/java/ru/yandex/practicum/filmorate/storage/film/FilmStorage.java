package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    void createFilm(Film film);

    void updateFilm(Film film);

    void removeFilm(Film film);

    List<Film> findAll();
}

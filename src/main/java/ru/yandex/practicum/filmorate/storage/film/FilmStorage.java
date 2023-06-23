package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void createFilm(Film film);

    void updateFilm(Film film);

    void removeFilm(Film film);

    List<Film> findAll();

    Film findById(int id);
}

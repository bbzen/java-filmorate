package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    void createFilm(Film film);

    void removeFilmById(int id);

    void updateFilm(Film film);
}

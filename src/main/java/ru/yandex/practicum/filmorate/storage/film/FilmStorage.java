package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    void removeFilm(int filmId);

    List<Film> findAll();

    List<Film> findAllByDirectorId(int dirId);

    Film findById(int id);

    List<Film> findMostPopular(Integer limit, Integer genreId, Integer releaseYear);

    boolean containsFilm(int id);

    List<Film> getRecommendations(int userId);

    List<Film> getCommonFilmList(int userId, int friendId);

    List<Film> findByDirTitle(String by, String query);
}

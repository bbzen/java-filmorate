package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public void createFilm(Film film) {
        doAllChecks(film);
        filmStorage.createFilm(film);
    }

    public void updateFilm(Film film) {
        doAllChecks(film);
        filmStorage.updateFilm(film);
    }

    public void removeFilm(Film film) {
        filmStorage.removeFilm(film);
    }

    public List<Film> getTopTen() {
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesAmount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public void addLike(Film film, User user) {
        film.addLike(user);
    }

    public void removeLike(Film film, User user) {
        film.removeLike(user);
    }

    private void checkName(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            log.debug("Получено пустое название фильма - {}.", film.getName());
            throw new ValidationException("Название не может быть пустым.");
        }
    }

    private void checkDescription(Film film) {
        if (200 < film.getDescription().length()) {
            log.debug("Длинна фильма превышает 200 символов - {}.", film.getDescription().length());
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
    }

    private void checkDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза фильма до 28-12-1985 - {}.", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
    }

    private void checkDuration(Film film) {
        if (film.getDuration() < 0) {
            log.debug("Продолжительность фильма - отрицательное число - {}.", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    private void doAllChecks(Film film) {
        checkName(film);
        checkDescription(film);
        checkDate(film);
        checkDuration(film);
    }
}

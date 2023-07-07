package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private static final int MIN_FILMS_COUNT = 10;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> findAll() {
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Film::getId))
                .collect(Collectors.toList());
    }

    public Film findById(int id) {
        return filmStorage.findById(id);
    }

    public List<Film> findTopFilms(Integer count) {
        int amount = MIN_FILMS_COUNT;
        if (count != null) {
            amount = count;
        }
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesAmount).reversed())
                .limit(amount)
                .collect(Collectors.toList());
    }

    public void createFilm(Film film) {
        doAllChecks(film);
        filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        doAllChecks(film);
        return filmStorage.updateFilm(film);
    }

    public void removeFilm(Film film) {
        filmStorage.removeFilm(film);
    }

    public Film addLike(int id, int userId) {
        Film film = filmStorage.findById(id);
        User user = userService.findById(userId);
        film.addLike(user);
        filmStorage.updateFilm(film);
        return film;
    }

    public void removeLike(int filmId, int userId) {
        Film currentFilm = filmStorage.findById(filmId);
        if (!(currentFilm.containsLike(userId))) {
            throw new UserNotFoundException("Указанный пользователь не лайкал указанный фильм.");
        }
        currentFilm.removeLike(userId);
        filmStorage.updateFilm(currentFilm);
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

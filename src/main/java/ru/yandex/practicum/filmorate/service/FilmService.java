package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Event.EventStorage;
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
    private final DirectorService directorService;
    private final EventStorage eventStorage;
    private static final int MIN_FILMS_COUNT = 10;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService, DirectorService directorService, EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.directorService = directorService;
        this.eventStorage = eventStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Film::getId))
                .collect(Collectors.toList());
    }

    public List<Film> findAllByDirector(int dirId, String sort) {
        directorService.containsDirector(dirId);
        List<Film> result = filmStorage.findAllByDirectorId(dirId);

        if (sort.equalsIgnoreCase("year")) {
            return result.stream().sorted(Comparator.comparingLong(Film::getReleaseDateEpochDays))
                    .collect(Collectors.toList());
        }
        if (sort.equalsIgnoreCase("likes")) {
            return result.stream().sorted(Comparator.comparingLong(Film::getLikesAmount))
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Заданный способ сортировки не найден, возможные варианты - year, likes");
    }

    public Film findById(int id) {
        return filmStorage.findById(id);
    }

    public List<Film> findMostPopular(Integer limit, Integer genreId, Integer releaseYear) {
        int amount = MIN_FILMS_COUNT;
        if (limit != null) {
            amount = limit;
        }
        return filmStorage.findMostPopular(amount, genreId, releaseYear);
    }

    public void createFilm(Film film) {
        doAllChecks(film);
        filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        doAllChecks(film);
        return filmStorage.updateFilm(film);
    }

    public void removeFilm(int filmId) {
        filmStorage.containsFilm(filmId);
        filmStorage.removeFilm(filmId);
    }

    public Film addLike(int id, int userId) {
        Film film = filmStorage.findById(id);
        User user = userService.findById(userId);
        film.addLike(user);
        filmStorage.updateFilm(film);
        eventStorage.createEvent(userId, "LIKE", "ADD", id);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film currentFilm = filmStorage.findById(filmId);
        if (!(currentFilm.containsLike(userId))) {
            throw new UserNotFoundException("Указанный пользователь не лайкал указанный фильм.");
        }
        currentFilm.removeLike(userId);
        filmStorage.updateFilm(currentFilm);
        eventStorage.createEvent(userId, "LIKE", "REMOVE", filmId);
        return currentFilm;
    }

    public List<Film> getCommon(Integer userId, Integer friendId) {
        userService.hasUser(userId);
        userService.hasUser(friendId);
        return filmStorage.getCommonFilmList(userId, friendId);
    }

    public List<Film> searchFilm(String by, String query) {
        return filmStorage.findByDirTitle(by, query);
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
        checkAddDirectors(film);
    }

    private void checkAddDirectors(Film film) {
        for (Director director : film.getDirectors()) {
            if (!directorService.containsDirector(director.getId())) {
                directorService.createDirector(director);
            }
        }
    }
}

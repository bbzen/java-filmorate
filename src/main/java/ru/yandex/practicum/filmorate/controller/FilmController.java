package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            if (isAllChecksDone(film) && !films.containsKey(film.getId())) {
                checkDescription(film);
                films.put(film.getId(), film);
            }
            return film;
        } catch (Exception e) {
            String message = "Ошибка валидации при добавлении фильма.";
            throw new ValidationException(message);
        }
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        try {
            if (isAllChecksDone(film) && films.containsKey(film.getId())) {
                checkDescription(film);
                films.put(film.getId(), film);
            }
            return film;
        } catch (Exception e) {
            String message = "Ошибка валидации при обновлении фильма.";
            throw new ValidationException(message);
        }
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    private boolean isNameEmpty(Film film) {
        return film.getName().isBlank() || film.getName() == null;
    }

    private void checkDescription(Film film) {
        String currentDescription = film.getDescription();
        if (200 < currentDescription.length()) {
            film.setDescription(currentDescription.substring(0, 200));
        }
    }

    private boolean isDateValid(Film film) {
        LocalDate anchorDate = LocalDate.of(1895, 12, 28);
        return film.getReleaseDate().isAfter(anchorDate);
    }

    private boolean isDurationPositive(Film film) {
        return !film.getDuration().isNegative();
    }

    private boolean isAllChecksDone(Film film) {
        return !isNameEmpty(film) && isDateValid(film) && isDurationPositive(film);
    }
}

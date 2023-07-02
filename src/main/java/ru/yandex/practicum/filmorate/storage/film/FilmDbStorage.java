package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    @Override
    public void createFilm(Film film) {

    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public void removeFilm(Film film) {

    }

    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Film findById(int id) {
        return null;
    }
}

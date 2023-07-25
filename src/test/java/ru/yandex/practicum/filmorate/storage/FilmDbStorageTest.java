package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;
    Film filmFirst;
    Film filmSecond;
    Film filmToUpdate;
    Mpa mpa;
    Genre genre;

    @BeforeEach
    void beforeEach() {
        mpa = new Mpa(1);
        genre = new Genre(2);
        filmFirst = new Film("First Movie Name", "First Movie description", LocalDate.of(2000, 5, 24), 120, new Mpa(1));
        filmSecond = new Film("Second Movie Name", "Second Movie description", LocalDate.of(2000, 5, 24), 120, new Mpa(1));
        filmToUpdate = new Film(1, "Movie Name upd", "Movie description upd", LocalDate.of(2000, 6, 24), 120L, 1, new Mpa(1), List.of(genre));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void createFilmAndFindById() {
        filmStorage.createFilm(filmFirst);
        Optional<Film> filmOptional = Optional.of(filmStorage.findById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", filmFirst.getName())
                                .hasFieldOrPropertyWithValue("description", filmFirst.getDescription())
                                .hasFieldOrPropertyWithValue("releaseDate", filmFirst.getReleaseDate())
                                .hasFieldOrPropertyWithValue("duration", filmFirst.getDuration())
                );
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateFilm() {
        filmStorage.createFilm(filmFirst);
        filmStorage.updateFilm(filmToUpdate);
        Optional<Film> filmOptional = Optional.of(filmStorage.findById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", filmToUpdate.getName())
                                .hasFieldOrPropertyWithValue("description", filmToUpdate.getDescription())
                                .hasFieldOrPropertyWithValue("releaseDate", filmToUpdate.getReleaseDate())
                                .hasFieldOrPropertyWithValue("duration", filmToUpdate.getDuration())
                );
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void removeFilm() {
        filmStorage.createFilm(filmFirst);
        Film toRemove = filmStorage.findById(1);
        filmStorage.removeFilm(toRemove.getId());

        try {
            filmStorage.findById(toRemove.getId());
        } catch (Exception e) {
            assertEquals("Фильм с ID 1 не найден.", e.getMessage());
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findAll() {
        filmStorage.createFilm(filmFirst);
        filmStorage.createFilm(filmSecond);

        List<Film> expectedFilms = List.of(filmStorage.findById(1), filmStorage.findById(2));
        assertEquals(expectedFilms, filmStorage.findAll());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testContainsFilm() {
        Film expected = filmStorage.createFilm(filmFirst);
        assertTrue(filmStorage.containsFilm(expected.getId()));
    }
}
package ru.yandex.practicum.filmorate.storage;

import de.cronn.testutils.h2.H2Util;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(SpringExtension.class)
@Import(H2Util.class)
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

    @AfterEach
    void resetDatabase(@Autowired H2Util h2Util) {
        h2Util.dropAllObjects();
    }

    @Test
    public void createFilm() {
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

    @Test
    void updateFilm() {
    }

    @Test
    void removeFilm() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }
}
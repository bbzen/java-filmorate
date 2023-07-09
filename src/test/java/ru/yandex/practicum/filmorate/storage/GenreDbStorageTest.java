package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
private final GenreDbStorage genreDbStorage;

    @Test
    void findById() {
        Genre expected = new Genre("Комедия", 1);
        assertEquals(expected, genreDbStorage.findById(1));
    }

    @Test
    void findAllGenres() {
        List<Genre> expected = List.of(new Genre("Комедия", 1), new Genre("Драма", 2),
                new Genre("Мультфильм", 3), new Genre("Триллер", 4),
                new Genre("Документальный", 5), new Genre("Боевик", 6));
        assertEquals(expected, genreDbStorage.findAllGenres());
    }
}
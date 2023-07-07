package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

class FilmControllerTest {
    private FilmController filmController;
    private FilmService filmService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
        filmService = new FilmService(new InMemoryFilmStorage(), userService);
        filmController = new FilmController(filmService);
    }

//    @Test
//    void createNormalFilm() {
//        Film filmExpected = new Film(0, "Movie Name", "Movie description", LocalDate.of(2000, 5, 24), 120, "G");
//        Film film = filmController.create(filmExpected);
//        filmExpected.setId(filmExpected.getId() + 1);
//        assertEquals(filmExpected, film);
//    }
//
//    @Test
//    void shouldIgnoreVoidRequest() {
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> filmController.create(null));
//        assertNull(exception.getMessage());
//    }
//
//    @Test
//    void shouldNotCreateIfNameIsEmpty() {
//        try {
//            filmController.create(new Film(0, "", "Movie description", LocalDate.of(2000, 5, 24), 120, "G"));
//        } catch (Exception e) {
//            assertEquals("Название не может быть пустым.", e.getMessage());
//        }
//    }
//
//    @Test
//    void shouldNotCreateIfDescriptionIsOverChared() {
//        try {
//            filmController.create(new Film(0, "Movie name", "Movie description is too long and contains too many characters, so it`s impossible to create such object. It is forbidden in this class. So after running it should throw a ValidationException. Let`s test it.", LocalDate.of(2000, 5, 24), 120, "G"));
//        } catch (Exception e) {
//            assertEquals("Максимальная длина описания — 200 символов.", e.getMessage());
//        }
//    }
//
//    @Test
//    void shouldNotCreateIfDateIsVeryLongAgo() {
//        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> filmController.create(new Film(0, "Movie Name", "Movie description", LocalDate.of(1895, 12, 27), 120, "G")));
//        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года.", runtimeException.getMessage());
//    }
//
//    @Test
//    void shouldNotCreateIfDurationIsNegative() {
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> filmController.create(new Film(0, "Movie Name", "Movie description", LocalDate.of(2005, 12, 27), -1, "G")));
//        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
//    }
//
//    @Test
//    void updateNormalFilm() {
//        filmController.create(new Film(0, "Movie Name", "Movie description", LocalDate.of(2000, 5, 24), 120, "G"));
//        Film filmExpected = new Film(1, "Movie updated", "Movie description updated", LocalDate.of(2000, 5, 25), 125, "G");
//        Film film = filmController.update(filmExpected);
//        assertEquals(filmExpected, film);
//    }
//
//    @Test
//    void shouldNotUpdateIfIdIsWrong() {
//        filmController.create(new Film(0, "Movie Name", "Movie description", LocalDate.of(2000, 5, 24), 120, "G"));
//        Film filmExpected = new Film(2, "Movie updated", "Movie description updated", LocalDate.of(2000, 5, 25), 125, "G");
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> filmController.update(filmExpected));
//        assertEquals("Данный фильм отсутствует в базе данных.", exception.getMessage());
//    }
}
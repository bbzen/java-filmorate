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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    User userFirst;
    User userSecond;
    User userToUpdate;
    Film film;
    Film filmToUpd;
    Mpa mpa;
    Genre genre;

    @BeforeEach
    void setUp() {
        userFirst = new User("FirstUserLogin", "First UserName", "firstuser@email.ru", LocalDate.of(1980, 5, 25));
        userSecond = new User("SecondUserLogin", "Second UserName", "seconduser@email.ru", LocalDate.of(1982, 7, 22));
        userToUpdate = new User(1, "UpdatedUserLogin", "Updated UserName", "updatedUser@email.ru", LocalDate.of(1980, 6, 25));
        mpa = new Mpa(1);
        genre = new Genre(2);
        film = new Film("Movie Name", "Movie description", LocalDate.of(2000, 5, 24), 120, new Mpa(3));
        filmToUpd = new Film(1, "Movie Name upd", "Movie description upd", LocalDate.of(2000, 6, 24), 120L, 1, new Mpa(3), List.of(genre));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testCreateAndFindUserById() {
        userStorage.createUser(userFirst);
        Optional<User> userOptional = Optional.of(userStorage.findUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("login", userFirst.getLogin())
                                .hasFieldOrPropertyWithValue("name", userFirst.getName())
                                .hasFieldOrPropertyWithValue("email", userFirst.getEmail())
                                .hasFieldOrPropertyWithValue("birthday", userFirst.getBirthday())
                );
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testUpdateUser() {
        userStorage.createUser(userFirst);
        userStorage.updateUser(userToUpdate);

        Optional<User> userOptional = Optional.of(userStorage.findUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("login", userToUpdate.getLogin())
                                .hasFieldOrPropertyWithValue("name", userToUpdate.getName())
                                .hasFieldOrPropertyWithValue("email", userToUpdate.getEmail())
                                .hasFieldOrPropertyWithValue("birthday", userToUpdate.getBirthday())
                );
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testRemoveUser() {
        userStorage.createUser(userFirst);
        User toRemove = userStorage.findUserById(1);
        userStorage.removeUser(toRemove.getId());

        try {
            userStorage.findUserById(1);
        } catch (Exception e) {
            assertEquals("User with id 1 not found.", e.getMessage());
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testFindAllUsersInDb() {
        userStorage.createUser(userFirst);
        userStorage.createUser(userSecond);

        List<User> expectingList = List.of(userStorage.findUserById(1), userStorage.findUserById(2));
        Collection<User> result = userStorage.findAll();
        assertEquals(expectingList, result);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testContainsUser() {
        User expectingUser = userStorage.createUser(userFirst);
        assertTrue(userStorage.containsUser(expectingUser.getId()));
    }
}
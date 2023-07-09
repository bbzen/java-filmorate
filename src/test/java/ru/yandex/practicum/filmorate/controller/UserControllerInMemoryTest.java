package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerInMemoryTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void shouldCreateNormalUser() {
        User user = new User(0, "user@email.ru", "UserLogin", "User Name", LocalDate.of(1980, 5, 25));
        User returnedUser = userController.create(user);
        user.setId(user.getId() + 1);
        assertEquals(user, returnedUser);
    }

    @Test
    void shouldSetLoginAsNameOfUser() {
        User user = new User(0, "user@email.ru", "UserLogin", "", LocalDate.of(1980, 5, 25));
        User returnedUser = userController.create(user);
        assertEquals(returnedUser.getLogin(), returnedUser.getName());
    }

    @Test
    void shouldIgnoreVoidRequest() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userController.create(null));
        assertNull(exception.getMessage());
    }

    @Test
    void shouldNotCreateWrongEmail() {
        User wrongUser1 = new User(0, "", "UserLogin", "", LocalDate.of(1980, 5, 25));
        User wrongUser2 = new User(0, "user-email.ru", "UserLogin", "", LocalDate.of(1980, 5, 25));
        RuntimeException exception1 = assertThrows(RuntimeException.class, () -> userController.create(wrongUser1));
        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> userController.create(wrongUser2));

        assertEquals("Адрес электронной почты не может быть пустой и должен содержать символ @", exception1.getMessage());
        assertEquals("Адрес электронной почты не может быть пустой и должен содержать символ @", exception2.getMessage());
    }

    @Test
    void shouldNotCreateWrongLogin() {
        User wrongUser1 = new User(0, "user@email.ru", "", "", LocalDate.of(1980, 5, 25));
        User wrongUser2 = new User(0, "user@email.ru", "User Login", "", LocalDate.of(1980, 5, 25));
        RuntimeException exception1 = assertThrows(RuntimeException.class, () -> userController.create(wrongUser1));
        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> userController.create(wrongUser2));

        assertEquals("Логин не может быть пустым и содержать пробелы", exception1.getMessage());
        assertEquals("Логин не может быть пустым и содержать пробелы", exception2.getMessage());
    }

    @Test
    void shouldIgnoreBdInFuture() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userController.create(new User(0, "user@email.ru", "UserLogin", "", LocalDate.of(3000, 5, 25))));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    @Test
    void shouldUpdateNormalUser() {
        userController.create(new User(0, "user@email.ru", "UserLogin", "User Name", LocalDate.of(1980, 5, 25)));
        User expectedUser = new User(1, "newUserEmail@email.ru", "newUserLogin", "NewUser Name", LocalDate.of(1980, 5, 25));
        User updatedUser = userController.update(expectedUser);
        assertEquals(expectedUser, updatedUser);
    }

    @Test
    void shouldNotUpdateIfIdIsWrong() {
        userController.create(new User(0, "user@email.ru", "UserLogin", "User Name", LocalDate.of(1980, 5, 25)));
        User expectedUser = new User(2, "UpdatedUserEmail@email.ru", "UpdatedUserLogin", "UpdatedUser Name", LocalDate.of(1980, 5, 25));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userController.update(expectedUser));
        assertEquals("Пользователь с ID 2 не зарегистрирован.", exception.getMessage());
    }
}


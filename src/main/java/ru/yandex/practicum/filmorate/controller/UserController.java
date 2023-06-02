package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        runAllChecks(user);
        checkName(user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        runAllChecks(user);
        if (!users.containsKey(user.getId())) {
            log.debug("Пользователь с ID {} не зарегистрирован.", user.getId());
            throw new ValidationException("Пользователь с ID " + user.getId() + " не зарегистрирован.");
        }
        checkName(user);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    private void checkName(User user) {
        String currentName = user.getName();
        if (currentName == null || currentName.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void isEmailEmpty(User user) {
        String currentEmail = user.getEmail();
        if (currentEmail == null || currentEmail.isBlank() || !currentEmail.contains("@")) {
            log.debug("Адрес электронной почты не корректный - {}", user.getEmail());
            throw new ValidationException("Адрес электронной почты не может быть пустой и должен содержать символ @");
        }
    }

    private void isLoginValid(User user) {
        String currentLogin = user.getLogin();
        if (currentLogin == null || currentLogin.isBlank() || currentLogin.contains(" ")) {
            log.debug("Не корректный логин - {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private void isBdValid(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Дата рождения не может быть в будущем - {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    private void runAllChecks(User user) {
        isEmailEmpty(user);
        isLoginValid(user);
        isBdValid(user);
    }


}

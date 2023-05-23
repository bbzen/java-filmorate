package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private int Id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        runAllChecks(user);
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с ID " + user.getId() + " уже зарегистрирован.");
        }
        checkName(user);
        setId(user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        runAllChecks(user);
        if (!users.containsKey(user.getId())) {
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

    private void setId(User user) {
        user.setId(++Id);
    }

    private void checkName(User user) {
        String currentName = user.getName();
        if (currentName == null || currentName.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private boolean isEmailNotEmpty(User user) {
        String currentEmail = user.getEmail();
        return currentEmail != null && !currentEmail.isBlank() && currentEmail.contains("@");
    }

    private boolean isLoginInvalid(User user) {
        String currentLogin = user.getLogin();
        return currentLogin == null || currentLogin.isBlank() || currentLogin.contains(" ");
    }

    private boolean isBdInFuture(User user) {
        return user.getBirthday().isAfter(LocalDate.now());
    }

    private void runAllChecks(User user) {
        if (!isEmailNotEmpty(user)) {
            throw new ValidationException("Адрес электронной почты не может быть пустой и должен содержать символ @");
        }
        if (isLoginInvalid(user)) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (isBdInFuture(user)) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}

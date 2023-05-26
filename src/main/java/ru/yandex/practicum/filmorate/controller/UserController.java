package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
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
    public User create(@RequestBody @Valid User user) {
        checkName(user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
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
}

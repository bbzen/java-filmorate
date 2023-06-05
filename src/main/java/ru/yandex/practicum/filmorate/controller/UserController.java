package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage userStorage;

    public UserController(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        userStorage.createUser(user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        userStorage.updateUser(user);
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return userStorage.findAll();
    }
}

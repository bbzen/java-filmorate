package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        if (user != null && !users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        if (user != null && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}

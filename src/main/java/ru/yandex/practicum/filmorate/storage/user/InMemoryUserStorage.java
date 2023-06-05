package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void createUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        containsUser(user.getId());
        users.put(user.getId(), user);
    }

    @Override
    public void removeUser(User user) {
        containsUser(user.getId());
        users.remove(user.getId());
    }

    @Override
    public User getUserById(int id) {
        containsUser(id);
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean containsUser(int id) {
        if (!users.containsKey(id)) {
            log.debug("Пользователь с ID {} не зарегистрирован.", id);
            throw new UserNotFoundException("Пользователь с ID " + id + " не зарегистрирован.");
        }
        return true;
    }
}

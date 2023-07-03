package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    void updateUser(User user);

    void removeUser(User user);

    User getUserById(int id);

    List<User> findAll();

    boolean containsUser(int id);
}

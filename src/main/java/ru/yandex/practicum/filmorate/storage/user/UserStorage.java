package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    void createUser(User user);

    void updateUser(User user);
}

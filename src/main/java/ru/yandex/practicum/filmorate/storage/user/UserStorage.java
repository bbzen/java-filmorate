package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    int createUser(User user);

    int removeUserById(int id);

    int updateUser(User user);
}

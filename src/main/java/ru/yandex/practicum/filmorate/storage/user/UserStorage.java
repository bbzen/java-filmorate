package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void removeUser(int userId);

    User findUserById(int id);

    Collection<User> findAll();

    void removeFS(int removerId, int acceptorId);

    boolean containsUser(int id);

    List<Event> getUserEvent(Integer id);

}

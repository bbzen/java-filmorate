package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    public List<Film> getRecommendations(int userId) {
        return filmStorage.getRecommendations(userId);
    }

    public User createUser(User user) {
        runAllChecks(user);
        checkName(user);
        return userStorage.createUser(user);
    }

    public void updateUser(User user) {
        runAllChecks(user);
        checkName(user);
        userStorage.updateUser(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        return userStorage.findUserById(id);
    }

    public Boolean hasUser(int userId) {
        return userStorage.containsUser(userId);
    }

    public List<User> findUserFriends(int id) {
        return userStorage.findUserById(id)
                .getFriends().stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }

    public List<User> findMutualFriends(int id, int otherId) {
        Collection<Integer> userOneFriends = userStorage.findUserById(id).getFriends();
        Collection<Integer> userTwoFriends = userStorage.findUserById(otherId).getFriends();
        List<User> result = new ArrayList<>();

        for (Integer friendId : userOneFriends) {
            if (userTwoFriends.contains(friendId)) {
                result.add(userStorage.findUserById(friendId));
            }
        }
        return result;
    }

    public void addToFriends(int acceptorId, int requesterId) {
        User acceptor = userStorage.findUserById(acceptorId);
        User requester = userStorage.findUserById(requesterId);
        if (userStorage.containsUser(acceptorId) && userStorage.containsUser(requesterId)) {
            acceptor.takeFsRequest(requesterId);
            requester.askFS(acceptorId);
            userStorage.updateUser(acceptor);
            userStorage.updateUser(requester);
            log.debug("Пользователь {} запросил дружбу у пользователя {}.", requesterId, acceptorId);
            log.debug("Пользователь {} добавил в друзья пользователя {}.", requesterId, acceptorId);
            eventStorage.createEvent(acceptorId, "FRIEND", "ADD", requesterId);

        }
    }

    public void removeFromFriends(int removerId, int toRemoveId) {
        User remover = userStorage.findUserById(removerId);
        User toRemove = userStorage.findUserById(toRemoveId);
        if (remover.hasFriend(toRemoveId) && toRemove.hasAcceptation(removerId)) {
            remover.removeFriend(toRemoveId);
            toRemove.removeAcceptation(removerId);
            userStorage.removeFS(removerId, toRemoveId);
            userStorage.updateUser(remover);
            userStorage.updateUser(toRemove);
            log.debug("Пользователь {} удалил из друзей пользователя {}.", remover.getLogin(), toRemove.getLogin());
            eventStorage.createEvent(removerId, "FRIEND", "REMOVE", toRemoveId);

        }
    }

    public void removeUser(int userId) {
        userStorage.containsUser(userId);
        userStorage.removeUser(userId);
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

    public List<Event> getUserEvent(Integer userId) {
        userStorage.findUserById(userId);
        return userStorage.getUserEvent(userId);
    }

    private void runAllChecks(User user) {
        isEmailEmpty(user);
        isLoginValid(user);
        isBdValid(user);
    }
}

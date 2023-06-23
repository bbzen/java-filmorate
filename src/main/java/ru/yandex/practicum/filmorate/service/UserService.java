package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void createUser(User user) {
        runAllChecks(user);
        checkName(user);
        userStorage.createUser(user);
    }

    public void updateUser(User user) {
        runAllChecks(user);
        checkName(user);
        userStorage.updateUser(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> findUserFriends(int id) {
        return userStorage.getUserById(id)
                .getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> findMutualFriends(int id, int otherId) {
        User userA = userStorage.getUserById(id);
        User userB = userStorage.getUserById(otherId);
        List<User> result = new ArrayList<>();

        for (Integer friendId : userA.getFriends()) {
            User temp = userStorage.getUserById(friendId);
            if (userB.containsFriend(temp)) {
                result.add(temp);
            }
        }
        return result;
    }

    public void addToFriends(int adder, int friendId) {
        if (userStorage.containsUser(adder) && userStorage.containsUser(friendId)) {
            userStorage.getUserById(adder).addFriend(friendId);
            userStorage.getUserById(friendId).addFriend(adder);
            log.debug("Пользователи {} {} успешно добавлены в друзья.", adder, friendId);
        }
    }

    public void removeFromFriends(int id, int otherId) {
        User userA = userStorage.getUserById(id);
        User userB = userStorage.getUserById(otherId);

        if (userStorage.containsUser(userA.getId()) && userStorage.containsUser(userB.getId())) {
            userA.removeFriend(userB);
            userB.removeFriend(userA);
            log.debug("Пользователи {} {} успешно удалены из друзей.", userA.getEmail(), userB.getEmail());
        }
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

    private void runAllChecks(User user) {
        isEmailEmpty(user);
        isLoginValid(user);
        isBdValid(user);
    }
}

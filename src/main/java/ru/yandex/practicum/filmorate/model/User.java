package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor(force = true)
public class User {
    private int id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    private final Set<Integer> requestedFriendship = new HashSet<>();;
    private final Set<Integer> acceptedFriendship = new HashSet<>();

    public User(String login, String name, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public boolean hasFriend(int id) {
        return requestedFriendship.contains(id);
    }

    public boolean hasAcceptation(int id) {
        return acceptedFriendship.contains(id);
    }

    public void askFS(int userId) {
        acceptedFriendship.add(userId);
    }

    public void takeFsRequest(int requesterId) {
        requestedFriendship.add(requesterId);
    }

    public void removeFriend(int userId) {
        try {
            requestedFriendship.remove(userId);
        } catch (Exception e ) {
            throw new UserNotFoundException("Пользователя " + userId + " нет в друзьях у пользователя " + this.getLogin());
        }
    }

    public void removeAcceptation(int userId) {
        try {
            acceptedFriendship.remove(userId);
        } catch (Exception e ) {
            throw new UserNotFoundException("Пользователя " + userId + " нет в друзьях у пользователя " + this.getLogin());
        }
    }

    public Collection<Integer> getFriends() {
        return requestedFriendship;
    }

    public void fillAcceptedFs(Collection<Integer> incomeCollection) {
        acceptedFriendship.addAll(incomeCollection);
    }

    public void fillRequestedFs(Collection<Integer> incomeCollection) {
        requestedFriendship.addAll(incomeCollection);
    }
}

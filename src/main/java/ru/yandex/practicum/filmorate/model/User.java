package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    private final Set<Integer> friends;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        friends = new HashSet<>();
    }

    public boolean containsFriend(User user) {
        return friends.contains(user.getId());
    }

    public boolean addFriend(User user) {
        return friends.add(user.getId());
    }

    public boolean removeFriend(User user) {
        return friends.remove(user.getId());
    }
}

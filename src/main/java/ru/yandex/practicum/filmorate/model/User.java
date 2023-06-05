package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private int id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    private final Set<Integer> friends;

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

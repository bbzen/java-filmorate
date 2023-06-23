package ru.yandex.practicum.filmorate.model;

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
    private final Set<Integer> requestedFriendship;
    private final Set<Integer> incomeFriendshipRequest;
    private final Set<Integer> friends;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        requestedFriendship = new HashSet<>();
        incomeFriendshipRequest = new HashSet<>();
        friends = new HashSet<>();
    }

    public boolean containsFriend(User user) {
        return friends.contains(user.getId());
    }

    public void addFriend(int userId) {
        friends.add(userId);
    }

    public void removeFriend(User user) {
        friends.remove(user.getId());
    }
}

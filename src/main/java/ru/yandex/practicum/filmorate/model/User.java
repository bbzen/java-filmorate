package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private final Set<Integer> requestedFriendship;
    private final Set<Integer> acceptedFriendship;


    public User(String login, String name, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        requestedFriendship = new HashSet<>();
        acceptedFriendship = new HashSet<>();
    }

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        requestedFriendship = new HashSet<>();
        acceptedFriendship = new HashSet<>();
    }



    public boolean containsFriend(User user) {
        return acceptedFriendship.contains(user.getId());
    }

    public boolean areUsersFriends(User user) {
        return acceptedFriendship.contains(user.getId()) && user.containsFriend(this);
    }

    public void incomeFSRequest(User user) {
        requestedFriendship.add(user.getId());
    }

    public void addFriend(User user) {
        acceptedFriendship.add(user.getId());
        requestedFriendship.add(user.getId());
    }

    public void removeFriend(User user) {
        acceptedFriendship.remove(user.getId());
    }
}

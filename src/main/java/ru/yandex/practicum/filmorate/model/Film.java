package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private final String name;
    private String description;
    private final LocalDate releaseDate;
    private final long duration;
    private final String rating;
    private final Set<Integer> whoLikedIt;
    private final Set<String> genre;

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, String rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating;
        whoLikedIt = new HashSet<>();
        genre = new HashSet<>();
    }

    public Film addLike(User user) {
        whoLikedIt.add(user.getId());
        return this;
    }

    public int getLikesAmount() {
        return whoLikedIt.size();
    }

    public boolean containsLike(int userId) {
        return whoLikedIt.contains(userId);
    }

    public void removeLike(int userId) {
        whoLikedIt.remove(userId);
    }
}

package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Film {
    private int id;
    private final String name;
    private String description;
    private final LocalDate releaseDate;
    private final long duration;
    private final Set<Integer> whoLikedIt;

    public Film(int id, String name, String description, LocalDate releaseDate, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        whoLikedIt = new HashSet<>();
    }

    public boolean addLike(User user) {
        return whoLikedIt.add(user.getId());
    }

    public int getLikesAmount() {
        return whoLikedIt.size();
    }

    public boolean containsLike(User user) {
        return whoLikedIt.contains(user.getId());
    }

    public boolean removeLike(User user) {
        return whoLikedIt.remove(user.getId());
    }
}

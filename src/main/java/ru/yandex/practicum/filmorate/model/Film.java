package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor(force = true)
public class Film {
    private int id;
    private final String name;
    private String description;
    private final LocalDate releaseDate;
    private final long duration;
    private final Map<String, Integer> mpa = new HashMap<>();
    private final Set<Integer> whoLikedIt = new HashSet<>();;
    private final Set<String> genre = new HashSet<>();;

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, Map<String, Integer> mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa.put("id", mpa.get("id"));
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

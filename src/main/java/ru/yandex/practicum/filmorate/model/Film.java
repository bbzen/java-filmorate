package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Film {
    private int id;
    private final String name;
    private String description;
    private final LocalDate releaseDate;
    private final long duration;
    private int rate;
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();
    private final Set<Integer> likes = new HashSet<>();
    private final List<Director> directors = new ArrayList<>();

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(String name, String description, LocalDate releaseDate, long duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film addLike(User user) {
        likes.add(user.getId());
        return this;
    }

    public int getLikesAmount() {
        return likes.size();
    }

    public boolean containsLike(int userId) {
        return likes.contains(userId);
    }

    public void removeLike(int userId) {
        likes.remove(userId);
    }

    public void applyMpaData(List<Mpa> mpaIncome) {
        if (mpaIncome.size() == 1) {
            this.mpa = mpaIncome.get(0);
        }
    }

    public void applyLikesData(List<Integer> likesIncome) {
        likes.addAll(likesIncome);
    }

    public void applyGenresData(List<Genre> genresIncome) {
        genres.addAll(genresIncome);
    }

    public void applyDirectorsData(List<Director> directorsIncome) {
        directors.addAll(directorsIncome);
    }

    public Integer getMpaId() {
        if (mpa != null) {
            return mpa.getId();
        }
        return null;
    }
}

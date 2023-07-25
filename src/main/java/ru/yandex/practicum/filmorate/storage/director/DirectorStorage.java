package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAllDirectors();

    Director findById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void removeDirector(int dirId);

    boolean containsDirector(int dirId);
}

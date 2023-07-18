package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public List<Director> findAllDirectors() {
        return directorDbStorage.findAllDirectors();
    }

    public Director findById(int id) {
        return directorDbStorage.findById(id);
    }

    public Director createDirector(Director director) {
        checkName(director);
        return directorDbStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        checkName(director);
        return directorDbStorage.updateDirector(director);
    }

    public void removeDirector(int dirId) {
        directorDbStorage.removeDirector(dirId);
    }

    public boolean containsDirector(int dirId) {
        return directorDbStorage.containsDirector(dirId);
    }

    private void checkName(Director director) {
        String currentName = director.getName();
        if (currentName == null || currentName.isBlank()) {
            throw new RuntimeException("Не корректное имя режиссера. Исправьте имя и повторите попытку.");
        }
    }
}

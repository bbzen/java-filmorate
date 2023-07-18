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


}

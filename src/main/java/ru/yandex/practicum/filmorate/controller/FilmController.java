package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        filmService.createFilm(film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        return filmService.findById(id);
    }

//    @GetMapping("/popular")
//    public List<Film> getPopular(@RequestParam(required = false) Integer count) {
//        return filmService.findTopFilms(count);
//    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(required = false) Integer count, @RequestParam(required = false) Integer genreId, @RequestParam(required = false) Integer year) {
        return filmService.findMostPopular(count, genreId, year);
    }
    @GetMapping("/common")
    public List<Film> getCommon(@RequestParam Integer userId,
                                @RequestParam Integer friendId) {
        return filmService.getCommon(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirector(@PathVariable int directorId, @RequestParam(required = false) String sortBy) {
        return filmService.findAllByDirector(directorId, sortBy);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable int id) {
        filmService.removeFilm(id);
    }
}

package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre findById(int id) {
        List<Genre> genres = jdbcTemplate.query("select * from genres where genre_id = ?", genreRowMapper(), id);
        if (genres.size() == 1) {
            return genres.get(0);
        }
        throw new GenreNotFoundException("Жанра с указанным Id нет в базе.");
    }

    @Override
    public List<Genre> findAllGenres() {
        return jdbcTemplate.query("select * from genres", genreRowMapper())
                .stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toList());
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(
                rs.getString("genre_name"),
                rs.getInt("genre_id")
        );
    }
}

package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

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
        return jdbcTemplate.query("select * from genres", genreRowMapper());
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(
                rs.getString("genre_name"),
                rs.getInt("genre_id")
        );
    }
}

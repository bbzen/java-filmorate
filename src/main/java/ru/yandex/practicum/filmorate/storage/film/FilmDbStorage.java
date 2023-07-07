package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
            Map<String, Object> params = Map.of("film_name", film.getName(), "film_description", film.getDescription(), "release_date", film.getReleaseDate(), "film_duration", film.getDuration(), "mpa", film.getMpa().get("id"));
            Number id = simpleJdbcInsert.executeAndReturnKey(params);
            film.setId(id.intValue());
        log.debug("Фильм " + film.getName() + " добавлен.");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        return film;
    }

    @Override
    public void removeFilm(Film film) {

    }

    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Film findById(int id) {
        List<Film> films = getFilmListById(id);

        return null;
    }

    private boolean containsFilm(int id) {
        getFilmListById(id);
        return true;
    }

    private List<Film> getFilmListById(int id) {
        String sql = "select * from films where film_id = ?";
        List<Film> film = jdbcTemplate.query(sql, filmRowMapper(), id);
        if (film.size() != 1) {
            throw new FilmNotFoundException("Фильм с ID " + id + " не найден.")
        }
        return film;
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("film_name"),
                rs.getString("film_description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("film_duration"),
                Map.of("id", rs.getInt("mpa"))
        );
    }
}

package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        SimpleJdbcInsert simpleFilmInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
            Map<String, Object> params = Map.of("film_name", film.getName(),
                    "film_description", film.getDescription(),
                    "release_date", film.getReleaseDate(),
                    "film_duration", film.getDuration(),
                    "film_mpa", film.getMpaId());
            Number id = simpleFilmInsert.executeAndReturnKey(params);
            film.setId(id.intValue());
            updateFilmGenre(film);
        log.debug("Фильм " + film.getName() + " добавлен.");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        containsFilm(film.getId());
            String sqlFilm = "update films set film_name = ?, film_description = ?, release_date = ?, film_duration = ?, film_mpa = ? where film_id = ?";
            jdbcTemplate.update(sqlFilm, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpaId(), film.getId());
            updateLikes(film);
            updateFilmGenre(film);
        return film;
    }

    @Override
    public void removeFilm(Film film) {

    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from films";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper());
        for (Film film : films) {
            applyMpaFromDb(film);
            applyLikesFromDb(film);
        }
        return films;
    }

    @Override
    public Film findById(int id) {
        Film film = getFilmListById(id).get(0);
        applyMpaFromDb(film);
        applyLikesFromDb(film);
        return film;
    }

    private boolean containsFilm(int id) {
        getFilmListById(id);
        return true;
    }

    private List<Film> getFilmListById(int id) {
        String sql = "select * from films where film_id = ?";
        List<Film> film = jdbcTemplate.query(sql, filmRowMapper(), id);
        if (film.size() != 1) {
            throw new FilmNotFoundException("Фильм с ID " + id + " не найден.");
        }
        return film;
    }

    private Film applyMpaFromDb(Film film) {
        List<Mpa> mpas = jdbcTemplate.query("select f.film_mpa, m.mpa_name from films f join mpa m on f.film_mpa = m.mpa_id where film_id = ?", mpaRowMapper(), film.getId());
        film.applyMpaData(mpas);
        return film;
    }

    private Film applyLikesFromDb(Film film) {
        List<Integer> likes = jdbcTemplate.query("select user_id from likes where film_id = ?", (rs, rowNum) -> rs.getInt("user_id"), film.getId());
        film.applyLikesData(likes);
        return film;
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("film_name"),
                rs.getString("film_description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("film_duration"),
                new Mpa(rs.getInt("film_mpa")));
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> new Mpa(
                rs.getString("mpa_name"),
                rs.getInt("film_mpa")
        );
    }

    private void updateLikes(Film film) {
        SimpleJdbcInsert simpleLikesInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("likes");
        if (!film.getLikes().isEmpty()) {
            for (Integer id : film.getLikes()) {
                Map<String, Integer> params = Map.of("film_id", film.getId(), "user_id", id);
                try {
                    simpleLikesInsert.execute(params);
                } catch (DuplicateKeyException e) {
                    log.debug("Повторяющееся значение пары ID пользователя и фильма не были добавлены.");
                }
            }
        }
    }

    private void updateFilmGenre(Film film) {
        SimpleJdbcInsert simpleGenreInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("film_genres");
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                Map<String, Integer> params = Map.of("film_id", film.getId(), "genre_id", genre.getId());
                try {
                    simpleGenreInsert.execute(params);
                } catch (DuplicateKeyException e) {
                    log.debug("Повторяющееся значение пары ID пользователя и жанра не были добавлены.");
                }
            }
        }
    }
}
package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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

    @Autowired
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
        updateFilmDirectors(film);
        log.debug("Фильм " + film.getName() + " добавлен.");
        return findById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        containsFilm(film.getId());
        String sqlFilm = "update films set film_name = ?, film_description = ?, release_date = ?, film_duration = ?, film_mpa = ? where film_id = ?";
        jdbcTemplate.update(sqlFilm, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpaId(), film.getId());
        updateLikes(film);
        updateFilmGenre(film);
        updateFilmDirectors(film);
        return findById(film.getId());
    }

    @Override
    public void removeFilm(int filmId) {
        String sql = "delete from films where film_id = ?";
        jdbcTemplate.update(sql, filmId);
        log.debug("Фильм " + filmId + " удален.");
    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from films";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper());
        for (Film film : films) {
            applyMpaFromDb(film);
            applyLikesFromDb(film);
            applyGenresFromDb(film);
            applyDirectorsFromDb(film);
        }
        return films;
    }

    @Override
    public List<Film> findAllByDirectorId(int dirId) {
        String sql = "select f.* FROM FILM_DIRECTORS fd JOIN films f ON f.FILM_ID = fd.FILM_ID WHERE DIR_ID = ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper(), dirId);
        for (Film film : films) {
            applyMpaFromDb(film);
            applyLikesFromDb(film);
            applyGenresFromDb(film);
            applyDirectorsFromDb(film);
        }
        return films;
    }

    @Override
    public Film findById(int id) {
        Film film = getFilmListById(id).get(0);
        applyMpaFromDb(film);
        applyLikesFromDb(film);
        applyGenresFromDb(film);
        applyDirectorsFromDb(film);
        return film;
    }

    @Override
    public boolean containsFilm(int id) {
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

    private Film applyGenresFromDb(Film film) {
        List<Genre> genres = jdbcTemplate.query("select fg.genre_id, g.genre_name from film_genres fg join genres g on g.genre_id = fg.genre_id where film_id = ?", genreRowMapper(), film.getId());
        film.applyGenresData(genres);
        return film;
    }

    private Film applyDirectorsFromDb(Film film) {
        List<Director> directors = jdbcTemplate.query("select d.DIR_ID, d.DIR_NAME FROM FILM_DIRECTORS fd join DIRECTORS d on fd.DIR_ID = d.DIR_ID where film_id = ?", directorRowMapper(), film.getId());
        film.applyDirectorsData(directors);
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

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(
                rs.getString("genre_name"),
                rs.getInt("genre_id")
        );
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(
                rs.getInt("dir_id"),
                rs.getString("dir_name")

        );
    }

    private void updateLikes(Film film) {
        jdbcTemplate.update("delete from likes where film_id = ?", film.getId());
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
        jdbcTemplate.update("delete from film_genres where film_id = ?", film.getId());
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

    private void updateFilmDirectors(Film film) {
        jdbcTemplate.update("delete from film_directors where film_id = ?", film.getId());
        SimpleJdbcInsert simpleGenreInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("film_directors");
        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                Map<String, Integer> params = Map.of("film_id", film.getId(), "dir_id", director.getId());
                try {
                    simpleGenreInsert.execute(params);
                } catch (DuplicateKeyException e) {
                    log.debug("Повторяющееся значение пары ID пользователя и жанра не были добавлены.");
                }
            }
        }
    }
}

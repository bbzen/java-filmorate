package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String filmsByGenreNYear = "SELECT f.*, COUNT(l.film_id) as likes_count\n" +
            "FROM films f\n" +
            "JOIN film_genres fg ON f.film_id = fg.film_id\n" +
            "JOIN genres g ON fg.genre_id = g.genre_id\n" +
            "LEFT JOIN likes l ON f.film_id = l.film_id\n" +
            "WHERE g.genre_id  = ? AND EXTRACT(YEAR FROM f.release_date) = ?\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY likes_count DESC\n" +
            "LIMIT ?";
    private final String filmsByYear = "SELECT f.*, COUNT(l.film_id) as likes_count\n" +
            "FROM films f\n" +
            "LEFT JOIN likes l ON f.film_id = l.film_id\n" +
            "WHERE EXTRACT(YEAR FROM f.release_date) = ?\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY likes_count DESC\n" +
            "LIMIT ?";
    private final String filmsByGenre = "SELECT f.*, COUNT(l.film_id) as likes_count\n" +
            "FROM films f\n" +
            "JOIN film_genres fg ON f.film_id = fg.film_id\n" +
            "JOIN genres g ON fg.genre_id = g.genre_id\n" +
            "LEFT JOIN likes l ON f.film_id = l.film_id\n" +
            "WHERE g.genre_id  = ? \n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY likes_count DESC\n" +
            "LIMIT ?";
    private final String filmLimited = "SELECT f.*, COUNT(l.film_id) as likes_count\n" +
            "FROM films f\n" +
            "LEFT JOIN likes l ON f.film_id = l.film_id\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY likes_count DESC\n" +
            "LIMIT ?";
    private final String filmsRecomented = "select f.* from likes l " +
            "join films f on f.film_id = l.film_id " +
            "where l.user_id = (select l2.user_id from likes l1 join likes l2 ON l1.film_id = l2.film_id " +
            "and l1.user_id != l2.user_id where l1.user_id = ? group by l1.user_id, l2.user_id " +
            "order by count(*) desc limit 1) and l.film_id not in (select film_id from likes where user_id = ?)";

    private final String filmsCommon = "select f.* " +
            "from films f " +
            "join likes l on l.film_id = f.film_id " +
            "where l.user_id in (?, ?) " +
            "group by f.film_id " +
            "having count(distinct l.user_id) = 2 " +
            "order by (select count(film_id) from likes where film_id = f.film_id) desc";

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
            applyAllDataFromDb(film);
        }
        return films;
    }

    @Override
    public List<Film> findAllByDirectorId(int dirId) {
        String sql = "select f.* FROM FILM_DIRECTORS fd JOIN films f ON f.FILM_ID = fd.FILM_ID WHERE DIR_ID = ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper(), dirId);
        for (Film film : films) {
            applyAllDataFromDb(film);
        }
        return films;
    }

    @Override
    public Film findById(int id) {
        Film film = getFilmListById(id).get(0);
        applyAllDataFromDb(film);
        return film;
    }

    @Override
    public List<Film> findMostPopular(Integer limit, Integer genreId, Integer releaseYear) {
        List<Film> films;
        if (genreId != null || releaseYear != null) {
            if (genreId != null && releaseYear != null) {
                films = jdbcTemplate.query(filmsByGenreNYear, filmRowMapper(), genreId, releaseYear, limit);
            } else if (releaseYear != null) {
                films = jdbcTemplate.query(filmsByYear, filmRowMapper(), releaseYear, limit);
            } else {
                films = jdbcTemplate.query(filmsByGenre, filmRowMapper(), genreId, limit);
            }
        } else {
            films = jdbcTemplate.query(filmLimited, filmRowMapper(), limit);
        }
        for (Film film : films) {
            applyAllDataFromDb(film);
        }
        return films;
    }

    @Override
    public boolean containsFilm(int id) {
        getFilmListById(id);
        return true;
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        List<Film> films = jdbcTemplate.query(filmsRecomented, filmRowMapper(), userId, userId);
        for (Film film : films) {
            applyMpaFromDb(film);
            applyLikesFromDb(film);
            applyGenresFromDb(film);
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilmList(int userId, int friendId) {
        List<Film> films = jdbcTemplate.query(filmsCommon, filmRowMapper(), userId, friendId);
        for (Film film : films) {
            applyAllDataFromDb(film);
        }
        return films;
    }

    @Override
    public List<Film> findByDirTitle(String by, String query) {
        StringBuilder sql = new StringBuilder("select f.* " +
                "from films f " +
                "left join film_directors fd on f.film_id = fd.film_id " +
                "left join directors d on fd.dir_id = d.dir_id ");
        String arg = "%" + query.toLowerCase() + "%";

        if (by.equals("title"))
            sql.append("where lower(f.film_name) like ? ");
        else if (by.equals("director"))
            sql.append("where lower(d.dir_name) like ? ");
        else if (by.equals("director,title") || by.equals("title,director"))
            sql.append("where lower(d.dir_name) like $1 or lower(f.film_name) like $1 ");
        else
            throw new ValidationException("Invalid value: '" + by + "' for parameter 'by'");

        sql.append("group by f.film_id " +
                "order by (select count(film_id) from likes where film_id = f.film_id) desc");

        List<Film> films = jdbcTemplate.query(sql.toString(), filmRowMapper(), arg);
        films.forEach(this::applyAllDataFromDb);
        return films;
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

    private void applyAllDataFromDb(Film incomefilm) {
        applyMpaFromDb(incomefilm);
        applyLikesFromDb(incomefilm);
        applyGenresFromDb(incomefilm);
        applyDirectorsFromDb(incomefilm);
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
            List<Map<String, Integer>> entries = new ArrayList<>();
            for (Integer id : film.getLikes()) {
                entries.add(Map.of("film_id", film.getId(), "user_id", id));
            }
            try {
                simpleLikesInsert.executeBatch(SqlParameterSourceUtils.createBatch(entries));
            } catch (DuplicateKeyException e) {
                log.debug("Повторяющееся значение пары ID пользователя и фильма не были добавлены.");
            }
        }
    }

    private void updateFilmGenre(Film film) {
        jdbcTemplate.update("delete from film_genres where film_id = ?", film.getId());
        SimpleJdbcInsert simpleGenreInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("film_genres");
        if (!film.getGenres().isEmpty()) {
            List<Map<String, Integer>> entries = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                entries.add(Map.of("film_id", film.getId(), "genre_id", genre.getId()));
            }
            try {
                simpleGenreInsert.executeBatch(SqlParameterSourceUtils.createBatch(entries));
            } catch (DuplicateKeyException e) {
                log.debug("Повторяющееся значение пары ID пользователя и жанра не были добавлены.");
            }
        }
    }

    private void updateFilmDirectors(Film film) {
        jdbcTemplate.update("delete from film_directors where film_id = ?", film.getId());
        SimpleJdbcInsert simpleGenreInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("film_directors");
        if (!film.getDirectors().isEmpty()) {
            List<Map<String, Integer>> entries = new ArrayList<>();
            for (Director director : film.getDirectors()) {
                entries.add(Map.of("film_id", film.getId(), "dir_id", director.getId()));
            }
            try {
                simpleGenreInsert.executeBatch(SqlParameterSourceUtils.createBatch(entries));
            } catch (DuplicateKeyException e) {
                log.debug("Повторяющееся значение пары ID пользователя и жанра не были добавлены.");
            }
        }
    }
}

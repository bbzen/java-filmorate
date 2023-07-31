package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director findById(int id) {
        List<Director> directors = jdbcTemplate.query("select * from directors where dir_id = ?", directorRowMapper(), id);
        if (directors.size() == 1) {
            return directors.get(0);
        }
        throw new DirectorNotFoundException("Режиссера с Id " + id + " нет в базе.");
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("directors")
                .usingGeneratedKeyColumns("dir_id");
        Map<String, Object> params = Map.of("dir_name", director.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        director.setId(id.intValue());
        log.debug("Режиссер " + director.getName() + " добавлен в базу данных.");
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        containsDirector(director.getId());
        String sqlDirector = "update directors set dir_name = ? where dir_id = ?";
        jdbcTemplate.update(sqlDirector, director.getName(), director.getId());
        log.debug("Данные режиссера " + director.getId() + " в БД обновлены.");
        return director;
    }

    @Override
    public void removeDirector(int dirId) {
        containsDirector(dirId);
        String sql = "delete from directors where dir_id = ?";
        jdbcTemplate.update(sql, dirId);
        log.debug("Режиссер " + dirId + " удален.");
    }

    @Override
    public boolean containsDirector(int dirId) {
        findById(dirId);
        return true;
    }

    @Override
    public List<Director> findAllDirectors() {
        List<Director> result = jdbcTemplate.query("select * from directors", directorRowMapper())
                .stream()
                .sorted((d1, d2) -> String.CASE_INSENSITIVE_ORDER.compare(d1.getName(), d2.getName()))
                .collect(Collectors.toList());
        return result;
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(
                rs.getInt("dir_id"),
                rs.getString("dir_name")

        );
    }
}

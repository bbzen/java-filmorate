package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Component
@Primary
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa findById(int id) {
        List<Mpa> mpas = jdbcTemplate.query("select * from mpa where mpa_id = ?", mpaRowMapper(), id);
        if (mpas.size() == 1) {
            return mpas.get(0);
        }
        throw new MpaNotFoundException("MPA с указанным Id нет в базе.");
    }

    @Override
    public List<Mpa> findAllMpa() {
        return jdbcTemplate.query("select * from mpa", mpaRowMapper());
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> new Mpa(
                rs.getString("mpa_name"),
                rs.getInt("mpa_id")
        );
    }
}

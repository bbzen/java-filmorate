package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, Object> params = Map.of("email", user.getEmail(), "login", user.getLogin(), "user_name", user.getName(), "birthday", user.getBirthday());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());
        log.debug("User " + user.getLogin() + " has been added.");
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "update users set email = ?, login = ?, user_name = ?, birthday = ? where user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.debug("User " + user.getLogin() + " was updated.");
        return user;
    }

    @Override
    public void removeUser(User user) {
        String sql = "delete from users where user_id = ?";
        jdbcTemplate.update(sql, user.getId());
        log.debug("User " + user.getLogin() + " has been removed.");
    }

    @Override
    public User getUserById(int id) {
        String sql = "select * from users where user_id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper(), id);
        if (users.size() != 1) {
            throw new UserNotFoundException("User with id " + id + " not found.");
        }
        return users.get(0);
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public boolean containsUser(int id) {
        return false;
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}

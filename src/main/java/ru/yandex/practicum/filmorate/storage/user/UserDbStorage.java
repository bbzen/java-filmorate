package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
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
        containsUser(user.getId());
        String sqlUsers = "update users set email = ?, login = ?, user_name = ?, birthday = ? where user_id = ?";
        jdbcTemplate.update(sqlUsers, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("friends");
        if (!user.getAcceptedFriendship().isEmpty()) {
            for (Integer id : user.getAcceptedFriendship()) {
                Map<String, Integer> params = Map.of("requester_id", user.getId(), "acceptor_id", id);
                try {
                    simpleJdbcInsert.execute(params);
                } catch (DuplicateKeyException e) {
                    log.debug("Повторяющееся значение пары ID пользователей при обновлении таблицы Friends.");
                }
            }
        }
        if (!user.getRequestedFriendship().isEmpty()) {
            for (Integer id : user.getRequestedFriendship()) {
                Map<String, Integer> params = Map.of("requester_id", id, "acceptor_id", user.getId());
                try {
                    simpleJdbcInsert.execute(params);
                } catch (DuplicateKeyException e) {
                    log.debug("Повторяющееся значение пары ID пользователей при обновлении таблицы Friends.");
                }
            }
        }
        log.debug("User " + user.getLogin() + " was updated.");
        return user;
    }

    @Override
    public void removeUser(int userId) {
        String sql = "delete from users where user_id = ?";
        jdbcTemplate.update(sql, userId);
        log.debug("Пользователь " + userId + " удален.");
    }

    @Override
    public User findUserById(int id) {
        List<User> users = getUserListById(id);
        User returnUser = users.get(0);
        List<Integer> friendsRequests = jdbcTemplate.query("select requester_id from friends where acceptor_id = ?", (rs, rowNum) -> rs.getInt("requester_id"), id);
        List<Integer> friendsAccepted = jdbcTemplate.query("select acceptor_id from friends where requester_id = ?", (rs, rowNum) -> rs.getInt("acceptor_id"), id);
        returnUser.fillRequestedFs(friendsRequests);
        returnUser.fillAcceptedFs(friendsAccepted);
        return returnUser;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "select * from users";
        List<User> users = jdbcTemplate.query(sql, userRowMapper());
        if (users.size() == 0) {
            throw new UserNotFoundException("There are no saved users.");
        }
        return users;
    }

    @Override
    public boolean containsUser(int id) {
        getUserListById(id);
        return true;
    }

    @Override
    public void removeFS(int removerId, int acceptorId) {
        String sql = "delete from friends where requester_id = ? and acceptor_id = ?";
        jdbcTemplate.update(sql, acceptorId, removerId);
    }

    @Override
    public List<Event> getUserEvent(Integer id) {
        String sqlQuery = "SELECT * FROM feeds WHERE userId = ?";
        return jdbcTemplate.query(sqlQuery, this::makeEvent, id);
    }

    private List<User> getUserListById(int id) {
        String sql = "select * from users where user_id = ?";
        List<User> user = jdbcTemplate.query(sql, userRowMapper(), id);
        if (user.size() != 1) {
            throw new UserNotFoundException("User with id " + id + " not found.");
        }
        return user;
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

    private Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getLong("userId"))
                .eventType(rs.getString("eventType"))
                .operation(rs.getString("operation"))
                .eventId(rs.getLong("eventId"))
                .entityId(rs.getLong("entityId"))
                .build();
    }
}

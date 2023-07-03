package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
//        String sqlUser = "insert into users (, , , ) values (?, ?, ? ,?)";
//        String sqlFriends = "insert into friends (requester_id, acceptor_id) values (?, ?)";
        Set<Integer> requestedFriendship = user.getRequestedFriendship();
        Set<Integer> acceptedFriendship = user.getAcceptedFriendship();


        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, Object> params = Map.of("email", user.getEmail(), "login", user.getLogin(), "user_name", user.getName(), "birthday", user.getBirthday());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());
        return user;
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void removeUser(User user) {

    }

    @Override
    public User getUserById(int id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public boolean containsUser(int id) {
        return false;
    }
}

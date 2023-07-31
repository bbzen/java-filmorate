package ru.yandex.practicum.filmorate.storage.Event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Component("eventDbStorage")
@Primary
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createEvent(int userId, String eventType, String operation, int entityId) {
        long timestamp = Timestamp.from(Instant.now()).getTime();
        String sqlQuery = "INSERT INTO feeds (userId, timestamp, eventType, operation, entityId) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, timestamp, eventType, operation, entityId);
        log.debug(timestamp + " - событие создано.");
    }


}
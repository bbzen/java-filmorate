package ru.yandex.practicum.filmorate.storage.Event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component("eventDbStorage")
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createEvent(int userId, String eventType, String operation, int entityId) {
        long timestamp = Timestamp.from(Instant.now()).getTime();
        String sqlQuery = "INSERT INTO feeds (userId, timestamp, eventType, operation, entityId) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, timestamp, eventType, operation, entityId);

    }
}
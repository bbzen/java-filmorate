package ru.yandex.practicum.filmorate.storage.event;

public interface EventStorage {

    void createEvent(int userId, String eventType, String operation, int entityId);
}
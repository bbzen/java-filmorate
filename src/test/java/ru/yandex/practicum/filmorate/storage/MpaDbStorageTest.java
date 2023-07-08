package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void findById() {
    Mpa expected = new Mpa("G", 1);
    assertEquals(expected, mpaDbStorage.findById(1));
    }

    @Test
    void findAllMpa() {
        List<Mpa> expectingList = List.of(new Mpa("G", 1), new Mpa("PG", 2), new Mpa("PG-13", 3), new Mpa("R", 4), new Mpa("NC-17", 5));
        assertEquals(expectingList, mpaDbStorage.findAllMpa());
    }
}
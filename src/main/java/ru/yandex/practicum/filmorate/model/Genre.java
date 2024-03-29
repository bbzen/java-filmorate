package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    private String name;
    private Integer id;

    public Genre(Integer id) {
        this.id = id;
    }
}


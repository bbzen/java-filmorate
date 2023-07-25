package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public class Review {
    private Integer reviewId;
    @NotBlank(message = "Отсутствует описание отзыва")
    private String content;
    @NotNull
    private boolean isPositive;
    @NotNull(message = "Не указан автор отзыва")
    private Integer userId;
    @NotNull(message = "Не указан фильм к каторому написан отзыв")
    private Integer filmId;
    private Integer useful;

    public Review() {
        super();
    }

    @JsonCreator
    public Review(String content, Boolean isPositive, Integer userId, Integer filmId, Integer useful) {
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }

    public Review(Integer reviewId, String content, Boolean isPositive, Integer userId,
                  Integer filmId, Integer useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }

    public boolean getIsPositive() {
        return isPositive;
    }
}

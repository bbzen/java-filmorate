package ru.yandex.practicum.filmorate.storage.Review;

public interface ReviewLikeDao {
    Integer addLike(Integer userId, Integer reviewId);
    Integer addDislikes(Integer userId, Integer reviewId);
    Integer delete(Integer userId, Integer reviewId);
}

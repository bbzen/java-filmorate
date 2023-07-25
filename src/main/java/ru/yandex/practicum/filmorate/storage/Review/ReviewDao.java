package ru.yandex.practicum.filmorate.storage.Review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewDao {
    Review getReviewById(Integer id);

    Collection<Review> getAllReview(Integer count);

    Collection<Review> getReviewByFilmId(Integer filmId, Integer count);

    Review addReview(Review review);

    Review updateReview(Review review);

    Integer deleteReviewById(Integer id);
}

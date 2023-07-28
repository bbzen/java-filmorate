package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.Event.EventStorage;
import ru.yandex.practicum.filmorate.storage.Review.ReviewDao;
import ru.yandex.practicum.filmorate.storage.Review.ReviewLikeDao;

import java.util.Collection;

@Slf4j
@Service
public class ReviewService {
    private final ReviewDao reviewDao;
    private final ReviewLikeDao reviewLikeDao;
    private final UserService userService;
    private final FilmService filmService;
    private final EventStorage eventStorage;

    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewLikeDao reviewLikeDao, UserService userService, FilmService filmService, EventStorage eventStorage) {
        this.reviewDao = reviewDao;
        this.reviewLikeDao = reviewLikeDao;
        this.userService = userService;
        this.filmService = filmService;
        this.eventStorage = eventStorage;
    }

    public Collection<Review> getReviews(Integer filmId, Integer count) {
        log.info("request GET /reviews?filmId={}&count={}", filmId, count);
        if (filmId < 1) {
            return reviewDao.getAllReview(count);
        }
        filmService.findById(filmId);
        return reviewDao.getReviewByFilmId(filmId, count);
    }

    public Review getReviewById(Integer id) {
        log.info("request GET /reviews/{}", id);
        return reviewDao.getReviewById(id);
    }

    public Review addReview(Review reviewDto) {
        log.info("request POST /reviews");
        log.debug("request body: {}", reviewDto);
        userService.findById(reviewDto.getUserId());
        filmService.findById(reviewDto.getFilmId());
        if (reviewDto.getReviewId() != null) {
            log.warn("При создании отзыва был передан id.");
            throw new ValidationException("При создание отзыва был передан id. Id создается автоматически.");
        }
        Review review = reviewDao.addReview(reviewDto);
        eventStorage.createEvent(reviewDto.getUserId(), "REVIEW", "ADD", review.getReviewId());
        return review;
    }

    public Review updateReview(Review review) {
        log.info("request PUT /reviews");
        log.debug("request body: {}", review);
        int reviewAuthorId = getReviewById(review.getReviewId()).getUserId();
        eventStorage.createEvent(reviewAuthorId, "REVIEW", "UPDATE", review.getReviewId());
        return reviewDao.updateReview(review);
    }

    public Integer deleteReviewById(Integer id) {
        log.info("request DELETE /reviews/{}", id);
        eventStorage.createEvent(getReviewById(id).getUserId(), "REVIEW", "REMOVE", getReviewById(id).getReviewId());
        return reviewDao.deleteReviewById(id);
    }

    public Integer addLike(Integer userId, Integer reviewId) {
        log.info("request PUT /reviews/{}/like/{}", reviewId, userId);
        getReviewById(reviewId);
        userService.findById(userId);
        return reviewLikeDao.addLike(userId, reviewId);
    }

    public Integer addDislike(Integer userId, Integer reviewId) {
        log.info("request PUT /reviews/{}/dislike/{}", reviewId, userId);
        getReviewById(reviewId);
        userService.findById(userId);
        return reviewLikeDao.addDislikes(userId, reviewId);
    }

    public Integer deleteLike(Integer userId, Integer reviewId) {
        log.info("request DELETE /reviews/{}/like/{}", reviewId, userId);
        getReviewById(reviewId);
        userService.findById(userId);
        return reviewLikeDao.delete(userId, reviewId);
    }

    public Integer deleteDislike(Integer userId, Integer reviewId) {
        log.info("request DELETE /reviews/{}/dislike/{}", reviewId, userId);
        getReviewById(reviewId);
        userService.findById(userId);
        return reviewLikeDao.delete(userId, reviewId);
    }
}

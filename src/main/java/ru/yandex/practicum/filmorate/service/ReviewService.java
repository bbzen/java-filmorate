package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
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

    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewLikeDao reviewLikeDao, UserService userService, FilmService filmService) {
        this.reviewDao = reviewDao;
        this.reviewLikeDao = reviewLikeDao;
        this.userService = userService;
        this.filmService = filmService;
    }

    public Collection<Review> getReviews(Integer filmId, Integer count) {
        if (filmId < 1) {
            log.info("Обработка запроса на отображение отзывов ко всем фильмам в количестве {}.", count);
            return reviewDao.getAllReview(count);
        }
        filmService.findById(filmId);
        log.info("Обработка запроса на отображение отзывов к фильму с ID {} в количестве {}.", filmId, count);
        return reviewDao.getReviewByFilmId(filmId, count);
    }

    public Review getReviewById(Integer id) {
        log.info("Обработка запроса на отображение отзыва c ID {}.", id);
        return reviewDao.getReviewById(id);
    }

    public Review addReview(Review review) {
        userService.findById(review.getUserId());
        filmService.findById(review.getFilmId());
        if (review.getReviewId() != null) {
            log.warn("При создании отзыва был передан id.");
            throw new ValidationException("При создание отзыва был передан id. Id создается автоматически.");
        }
        log.info("Обработка запроса на добавление нового отзыва.");
        return reviewDao.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewDao.updateReview(review);
    }

    public Integer deleteReviewById(Integer id) {
        return reviewDao.deleteReviewById(id);
    }

    public Integer addLike(Integer userId, Integer  reviewId) {
        getReviewById(reviewId);
        userService.findById(userId);
        log.info("Обработка запроса на добавление лайка отзыву с ID {} от пользователя с ID {}.", reviewId, userId);
        return reviewLikeDao.addLike(userId, reviewId);
    }

    public Integer addDislike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findById(userId);
        log.info("Обработка запроса на добавление дизлайка отзыву с ID {} от пользователя с ID {}.", reviewId, userId);
        return reviewLikeDao.addDislikes(userId, reviewId);
    }

    public Integer deleteLike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findById(userId);
        log.info("Обработка запроса на удаление лайка отзыву с ID {} от пользователя с ID {}.", reviewId, userId);
        return reviewLikeDao.delete(userId, reviewId);
    }

    public Integer deleteDislike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findById(userId);
        log.info("Обработка запроса на удаление дизлайка отзыву с ID {} от пользователя с ID {}.", reviewId, userId);
        return reviewLikeDao.delete(userId, reviewId);
    }
}

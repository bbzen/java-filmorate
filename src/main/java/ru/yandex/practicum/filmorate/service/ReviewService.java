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

    public Review addReview(Review reviewDto) {
        userService.findById(reviewDto.getUserId());
        filmService.findById(reviewDto.getFilmId());
        if (reviewDto.getReviewId() != null) {
            log.warn("При создании отзыва был передан id.");
            throw new ValidationException("При создание отзыва был передан id. Id создается автоматически.");
        }
        log.info("Обработка запроса на добавление нового отзыва.");
        Review review = reviewDao.addReview(reviewDto);
        eventStorage.createEvent(reviewDto.getUserId(), "REVIEW", "ADD", review.getReviewId());
        return review;
    }

    public Review updateReview(Review review) {
        int reviewAuthorId = getReviewById(review.getReviewId()).getUserId();
        eventStorage.createEvent(reviewAuthorId, "REVIEW", "UPDATE", review.getReviewId());
        return reviewDao.updateReview(review);
    }

    public Integer deleteReviewById(Integer id) {
        eventStorage.createEvent(getReviewById(id).getUserId(), "REVIEW", "REMOVE", getReviewById(id).getReviewId());
        return reviewDao.deleteReviewById(id);
    }

    public Integer addLike(Integer userId, Integer reviewId) {
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

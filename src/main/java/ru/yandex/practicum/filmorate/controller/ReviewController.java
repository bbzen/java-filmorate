package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public Collection<Review> getReviews(@RequestParam(defaultValue = "0") Integer filmId,
                                         @RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен запрос на отображение отзывав к фильму {} в количестве {}.", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        log.info("Получен запрос на отображение отзывава c ID {}.", id);
        return reviewService.getReviewById(id);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на добавление нового отзыва.");
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на обновление отзывава.");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public Integer deleteReviewById(@PathVariable Integer id) {
        log.info("Получен запрос на удаление отзывава c ID {}.", id);
        return reviewService.deleteReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Integer addLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Получен запрос на добавление лайка отзыва с ID {} от пользователя с ID {}.", reviewId, userId);
        return reviewService.addLike(userId, reviewId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Integer deleteLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Получен запрос на удаление лайка отзыва с ID {} от пользователя с ID {}.", reviewId, userId);
        return reviewService.deleteLike(userId, reviewId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Integer addDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Получен запрос на добавление дизлайка отзыва с ID {} от пользователя с ID {}.", reviewId, userId);
        return reviewService.addDislike(userId, reviewId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Integer deleteDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Получен запрос на удаление дизлайка отзыва с ID {} от пользователя с ID {}.", reviewId, userId);
        return reviewService.deleteDislike(userId, reviewId);
    }
}

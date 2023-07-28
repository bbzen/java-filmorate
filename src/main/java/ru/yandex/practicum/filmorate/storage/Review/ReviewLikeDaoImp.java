package ru.yandex.practicum.filmorate.storage.Review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ReviewLikeDaoImp implements ReviewLikeDao {
    private final JdbcTemplate jdbcTemplate;
    private final String addLikeQuery = "MERGE INTO review_like (user_id, review_id, is_like) " +
            "VALUES (?, ?, true);";
    private final String addDislikeQuery = "MERGE INTO review_like (user_id, review_id, is_like) " +
            "VALUES  (?, ?, false);";
    private final String deleteQuery = "DELETE FROM review_like WHERE user_id = ? AND review_id = ?;";

    @Autowired
    public ReviewLikeDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer addLike(Integer userId, Integer reviewId) {
        jdbcTemplate.update(addLikeQuery, userId, reviewId);
        return reviewId;
    }

    @Override
    public Integer addDislikes(Integer userId, Integer reviewId) {
        jdbcTemplate.update(addDislikeQuery, userId, reviewId);
        return reviewId;
    }

    @Override
    public Integer delete(Integer userId, Integer reviewId) {
        jdbcTemplate.update(deleteQuery, userId, reviewId);
        return reviewId;
    }
}

package ru.yandex.practicum.filmorate.storage.Review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Repository
public class ReviewDaoImp implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String Get_REVIEW_BY_ID_QUERY =
            "SELECT r.review_id," +
                    " r.user_id," +
                    " r.film_id," +
                    " r.content," +
                    " r.is_positive," +
                    " SUM(CASE WHEN rl.is_like = true THEN 1 ELSE 0 END) - " +
                    " SUM(CASE WHEN rl.is_like = false THEN 1 ELSE 0 END) AS useful " +
                    "FROM reviews AS r " +
                    "LEFT JOIN review_like AS rl ON r.review_id = rl.review_id " +
                    "WHERE r.review_id = ? " +
                    "GROUP BY r.review_id";
    private final static String GET_ALL_REVIEWS_QUERY =
            "SELECT r.review_id, " +
                    "r.user_id, " +
                    "r.film_id, " +
                    "r.content, " +
                    "r.is_positive, " +
                    "SUM(CASE WHEN rl.is_like = true THEN 1 ELSE 0 END) - " +
                    "SUM(CASE WHEN rl.is_like = false THEN 1 ELSE 0 END) AS useful " +
                    "FROM reviews AS r " +
                    "LEFT JOIN review_like AS rl ON r.review_id = rl.review_id " +
                    "GROUP BY r.review_id " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
    private final static String GET_REVIEW_BY_FILM_QUERY =
            "SELECT r.review_id, " +
                    "r.user_id, " +
                    "r.film_id, " +
                    "r.content, " +
                    "r.is_positive, " +
                    "SUM(CASE WHEN rl.is_like = true THEN 1 ELSE 0 END) - " +
                    "SUM(CASE WHEN rl.is_like = false THEN 1 ELSE 0 END) AS useful " +
                    "FROM reviews AS r " +
                    "LEFT JOIN review_like AS rl ON r.review_id = rl.review_id " +
                    "WHERE r.film_id = ? " +
                    "GROUP BY r.review_id " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
    private final static String ADD_REVIEW_QUERY =
            "INSERT INTO reviews (user_id, film_id, content, is_positive) " +
                    "VALUES (?, ?, ?, ?)";
    private final static String UPDATE_REVIEW_QUERY =
            "UPDATE reviews " +
                    "SET content = ?, " +
                    "is_positive = ? " +
                    "WHERE review_id =?";
    private final static String DELETE_REVIEW_BY_ID_QUERY = "DELETE FROM reviews WHERE review_id = ?";

    @Autowired
    public ReviewDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        try {
            return jdbcTemplate.queryForObject(Get_REVIEW_BY_ID_QUERY, this::mapRowToReview, reviewId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Не найден отзыв с ID {}", reviewId);
            throw new UserNotFoundException("ОТзыв с ID " + reviewId + " не найден.");
        }
    }

    @Override
    public Collection<Review> getAllReview(Integer count) {
        log.info("Запрос на отоброжение отзывов ко всем фильмам в количестве {} обработан.", count);
        return jdbcTemplate.query(GET_ALL_REVIEWS_QUERY, this::mapRowToReview, count);
    }

    @Override
    public Collection<Review> getReviewByFilmId(Integer filmId, Integer count) {
        log.info("Запрос на отоброжение отзывов к фильму с ID {} в количестве {} обработан.", filmId, count);
        return jdbcTemplate.query(GET_REVIEW_BY_FILM_QUERY, this::mapRowToReview, filmId, count);
    }

    @Override
    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(ADD_REVIEW_QUERY, new String[]{"review_id"});
            statement.setInt(1, review.getUserId());
            statement.setInt(2, review.getFilmId());
            statement.setString(3, review.getContent());
            statement.setBoolean(4, review.getIsPositive());
            return statement;
        }, keyHolder);
        int reviewId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        log.debug("Добавлен новый отзыв {}.", reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public Review updateReview(Review review) {
        jdbcTemplate.update(UPDATE_REVIEW_QUERY, review.getContent(), review.getIsPositive(), review.getReviewId());
        log.debug("Отзыв {} обнавлен.", review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public Integer deleteReviewById(Integer id) {
        jdbcTemplate.update(DELETE_REVIEW_BY_ID_QUERY, id);
        log.debug("Отзыв {} удален.", id);
        return id;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        int reviewId = resultSet.getInt("review_id");
        int userId = resultSet.getInt("user_id");
        int filmId = resultSet.getInt("film_id");
        String content = resultSet.getString("content");
        boolean isPositive = resultSet.getBoolean("is_positive");
        int useful = resultSet.getInt("useful");
        return new Review(reviewId, content, isPositive, userId, filmId, useful);
    }
}

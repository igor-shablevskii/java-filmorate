package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Repository
public class ReviewDbStorage implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Review save(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id ," +
                "film_id, useful) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            stmt.setInt(5, review.getUseful());
            return stmt;
        }, keyHolder);
        int reviewId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        review.setReviewId(reviewId);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer count, Integer filmId) {
        String sql = "SELECT * FROM reviews WHERE film_id = ?  LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId, count);
    }

    @Override
    public List<Review> getReviews(Integer count) {
        String sql = "SELECT * FROM reviews LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, count);
    }

    @Override
    public Review getReviewById(int reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToReview, reviewId);
    }

    @Override
    public void deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public boolean containsInStorage(int reviewId) {
        String sqlQuery = "SELECT count(*) FROM reviews WHERE review_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId);
        return result == 1;
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .userReactions(new HashSet<>())
                .build();
    }
}
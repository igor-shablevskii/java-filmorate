package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReactionDao;
import ru.yandex.practicum.filmorate.model.Reaction;
import ru.yandex.practicum.filmorate.model.ReactionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReactionDbStorage implements ReactionDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReactionDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Reaction> getReactions(Long reviewId) {
        String sql = "SELECT * FROM reviews_reactions " +
                     "WHERE review_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToReactionOnReview, reviewId);
    }

    @Override
    public void saveLike(Long reviewId, Long userId) {
        String sql = "INSERT INTO reviews_reactions " +
                     "(review_id, user_id, reaction_type) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, ReactionType.LIKE.name());
        updateUseful(reviewId);
    }

    @Override
    public void saveDislike(Long reviewId, Long userId) {
        String sql = "INSERT INTO reviews_reactions " +
                     "(review_id, user_id, reaction_type) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, ReactionType.DISLIKE.name());
        updateUseful(reviewId);
    }

    @Override
    public void deleteReaction(Long reviewId, Long userId) {
        String sql = "DELETE from reviews_reactions " +
                     "WHERE review_id=? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public boolean containsReactionInStorage(Long reviewId, Long userId) {
        String sqlQuery = "SELECT count(*) FROM reviews_reactions " +
                          "WHERE review_id = ? AND user_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId, userId);
        return result == 1;
    }

    private void updateUseful(Long reviewId) {
        String sqlQuery = "UPDATE reviews r " +
                "SET useful = (SELECT " +
                "(SELECT count(rr.review_id) " +
                "FROM reviews_reactions rr " +
                "WHERE rr.review_id = ? " +
                "AND rr.reaction_type = 'LIKE') - " +
                "(SELECT count(rr.review_id) " +
                "FROM reviews_reactions rr " +
                "WHERE rr.review_id = ? " +
                "AND rr.reaction_type = 'DISLIKE'))" +
                "WHERE r.review_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId, reviewId, reviewId);
    }

    private Reaction mapRowToReactionOnReview(ResultSet rs, int rowNum)
            throws SQLException {
        return Reaction.builder()
                .reaction(ReactionType.valueOf(rs.getString("reaction_type")))
                .userId(rs.getLong("user_id"))
                .build();
    }
}

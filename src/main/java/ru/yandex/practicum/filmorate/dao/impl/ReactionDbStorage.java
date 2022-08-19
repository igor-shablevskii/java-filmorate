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
    public List<Reaction> getReactions(Integer reviewId) {
        String sql = "SELECT * FROM reviews_reactions WHERE review_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToReactionOnReview, reviewId);
    }

    @Override
    public void saveLike(int reviewId, int userId) {
        String sql = "INSERT INTO reviews_reactions (review_id, user_id, reaction_type) values (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, "LIKE");
    }

    @Override
    public void saveDislike(int reviewId, int userId) {
        String sql = "INSERT INTO reviews_reactions (review_id, user_id, reaction_type) values (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, "DISLIKE");
    }

    @Override
    public void deleteReaction(int reviewId, int userId) {
        String sql = "DELETE from reviews_reactions where review_id=? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public boolean containsReactionInStorage(int reviewId, int userId) {
        String sqlQuery = "SELECT count(*) FROM reviews_reactions WHERE review_id = ? AND user_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId, userId);
        return result == 1;
    }

    private Reaction mapRowToReactionOnReview(ResultSet rs, int rowNum)
            throws SQLException {
        return Reaction.builder()
                .reaction(ReactionType.valueOf(rs.getString("reaction_type")))
                .userId(rs.getInt("user_id"))
                .build();
    }
}

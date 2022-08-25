package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

@Slf4j
@Repository
public class FeedDbStorage implements FeedDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Feed> getAllFeedsByUserId(Long id) {
        String sql = "SELECT * FROM feeds WHERE user_id = ? ORDER BY feed_id";
        return jdbcTemplate.query(sql, this::mapRowToFeed, id);
    }

    @Override
    public void create(Feed feed) {
        String sql = "INSERT INTO feeds(user_id, feed_time, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                feed.getUserId(),
                feed.getTimestamp(),
                feed.getEventType().name(),
                feed.getOperation().name(),
                feed.getEntityId());
    }

    @Override
    public boolean containsInStorage(Long id) {
        String sqlQuery = "SELECT count(*) FROM feeds WHERE feed_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getLong("feed_id"))
                .userId(rs.getLong("user_id"))
                .timestamp(rs.getLong("feed_time"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
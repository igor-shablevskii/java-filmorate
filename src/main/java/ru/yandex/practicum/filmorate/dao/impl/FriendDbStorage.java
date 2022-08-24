package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendDao;

import java.util.List;

@Repository
public class FriendDbStorage implements FriendDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<Long> getFriendsByUserId(Long userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?;";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }

    @Override
    public boolean containsInStorage(Long filmId, Long friendId) {
        String sqlQuery = "SELECT count(*) FROM friends WHERE user_id = ? AND friend_id;";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId, friendId);
        return result == 1;
    }
}
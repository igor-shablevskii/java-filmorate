package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MarkDao;

@Repository
public class MarkDbStorage implements MarkDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MarkDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Long filmId, Long userId, Byte mark) {
        String sql = "INSERT INTO film_marks (film_id, user_id, mark) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, filmId, userId, mark);
        updateRate(filmId);
    }

    @Override
    public void remove(Long filmId, Long userId) {
        String sql = "DELETE FROM film_marks WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        updateRate(filmId);
    }

    @Override
    public void update(Long filmId, Long userId, Byte mark) {
        String sql = "UPDATE film_marks SET mark = ? WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, mark, filmId, userId);
        updateRate(filmId);
    }

    @Override
    public boolean containsInStorage(Long filmId, Long userId) {
        String sqlQuery = "SELECT count(*) FROM film_marks WHERE film_id = ? AND user_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId, userId);
        return result == 1;
    }

    private void updateRate(Long filmId) {
        String sqlQuery = "UPDATE films f " +
                "SET f.rate = (SELECT CAST(avg(mark) AS REAL) FROM film_marks fm WHERE fm.film_id = ?) " +
                "WHERE f.film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, filmId);
    }
}
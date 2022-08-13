package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikeDao;

import java.util.List;

@Repository
public class LikeBbStorage implements LikeDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeBbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Integer getCountLikesByFilmId(int filmId) {
        String sql = "SELECT count(user_id) FROM film_likes WHERE film_id = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }

    @Override
    public List<Integer> getAllUsersLikeFilm(int film_id) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?;";
        return jdbcTemplate.queryForList(sql, Integer.class, film_id);
    }

    @Override
    public boolean containsInStorage(int filmId, int userId) {
        String sqlQuery = "SELECT count(*) FROM film_likes WHERE film_id = ? AND user_id = ?;";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId, userId);
        return result == 1;
    }
}

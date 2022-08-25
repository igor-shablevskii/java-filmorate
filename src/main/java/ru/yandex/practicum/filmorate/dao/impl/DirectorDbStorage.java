package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class DirectorDbStorage implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void setDirector(Film film) {
        String delSql = "DELETE FROM film_director WHERE film_id = ?;";
        jdbcTemplate.update(delSql, film.getId());

        Set<Director> directors = film.getDirectors();
        if (directors.size() > 0) {
            for (Director director : directors) {
                String addSql = "INSERT INTO film_director(film_id, director_id) values (?, ?); ";
                jdbcTemplate.update(addSql, film.getId(), director.getId());
            }
        }
    }

    @Override
    public List<Director> loadDirectors(Long filmId) {
        String sql = "SELECT d.director_id, d.director_name FROM film_director fd " +
                "LEFT JOIN directors d ON d.director_id = fd.director_id WHERE fd.film_id = ? ORDER BY d.director_id;";
        return jdbcTemplate.query(sql, this::mapRowToDirector, filmId);
    }

    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO directors(director_name) values (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?;";
        jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
        return director;
    }

    @Override
    public Director getDirectorById(Long id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?;";
        return jdbcTemplate.queryForObject(sql, this::mapRowToDirector, id);
    }

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM directors ORDER BY director_id; ";
        return jdbcTemplate.query(sql, this::mapRowToDirector);
    }

    @Override
    public void removeDirectorById(Long id) {
        String sql = "DELETE FROM directors where director_id = ?;";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean containsInStorage(Long id) {
        String sqlQuery = "SELECT count(*) FROM directors WHERE director_id = ?;";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}
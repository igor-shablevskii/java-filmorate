package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
public class GenreDbStorage implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void setGenre(Film film) {
        String delSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(delSql, film.getId());

        Set<Genre> genres = film.getGenres();
        if (genres.size() != 0) {
            for (Genre genre : genres) {
                String addSql = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(addSql, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public List<Genre> loadGenres(Long id) {
        String sql = "SELECT g.genre_id, g.genre_name FROM film_genre fg " +
                "LEFT JOIN genres g ON g.genre_id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, this::mapRowToGenre, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
    }

    @Override
    public boolean containsInStorage(Integer id) {
        String sqlQuery = "SELECT count(*) FROM genres WHERE genre_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}
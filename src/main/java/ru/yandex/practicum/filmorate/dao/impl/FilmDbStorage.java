package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        String sql = "INSERT INTO films (film_name, film_description, film_releasedate, film_duration, mpa_id)" +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET film_name = ?, film_description = ?, film_releasedate = ?," +
                "film_duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id;";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "SELECT * FROM films f left JOIN mpa_ratings m ON F.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?;";
        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
    }

    @Override
    public boolean containsInStorage(int filmId) {
        String sqlQuery = "SELECT count(*) FROM films WHERE film_id = ?;";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);
        return result == 1;
    }

    @Override
    public List<Film> getSortedFilmsByDirectors(int directorId, String sortBy) {
        List<Film> listFilm;
        if (sortBy.equals("year")) {
            String sql = "SELECT f.film_id, f.film_name, f.film_description, f.film_releasedate, " +
                    "f.film_duration, f.mpa_id, mr.mpa_name FROM films f " +
                    "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                    "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.film_releasedate;";
            listFilm = jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
        } else {
            String sql = "SELECT f.film_id, f.film_name, f.film_description, f.film_releasedate, " +
                    "f.film_duration, f.mpa_id, mr.mpa_name FROM films f " +
                    "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                    "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                    "LEFT JOIN film_director fd ON f.FILM_ID = fd.FILM_ID " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY fd.film_id " +
                    "ORDER BY count(DISTINCT fl.user_id) DESC;";
            listFilm = jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
        }
        return listFilm;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT " +
                "f.film_id, " +
                "f.film_name, " +
                "f.film_description, " +
                "f.film_releasedate, " +
                "f.film_duration, " +
                "f.mpa_id, " +
                "mr.mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY count(DISTINCT fl.user_id) DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("film_description"))
                .releaseDate(rs.getDate("film_releasedate").toLocalDate())
                .duration(rs.getInt("film_duration"))
                .mpa(new Mpa(
                        rs.getInt("mpa_id"),
                        rs.getString("mpa_name")))
                .genres(new LinkedHashSet<>())
                .directors(new HashSet<>())
                .build();
    }
}
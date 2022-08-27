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
        String sql = "INSERT INTO films (film_name, film_description, film_releasedate, film_duration, rate, mpa_id)" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setFloat(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET film_name = ?, film_description = ?," +
                " film_releasedate = ?,film_duration = ?, rate = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sql = "SELECT * FROM films f left JOIN mpa_ratings m ON F.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
    }

    @Override
    public boolean containsInStorage(Long filmId) {
        String sqlQuery = "SELECT count(*) FROM films WHERE film_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);
        return result == 1;
    }

    @Override
    public List<Film> getSortedDirectorsFilmsByLikes(Long directorId) {
        String sql = "SELECT f.*, mr.mpa_name FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "LEFT JOIN film_director fd ON f.FILM_ID = fd.FILM_ID " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.rate";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getSortedDirectorsFilmsByMarks(Long directorId) {
        String sql = "SELECT f.*, mr.mpa_name FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_marks fl ON f.film_id = fl.film_id " +
                "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = ? AND fl.mark > 5 " +
                "ORDER BY f.rate";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getSortedDirectorsFilmsByYears(Long directorId) {
        String sql = "SELECT f.*, mr.mpa_name FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY YEAR(f.FILM_RELEASEDATE)";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "SELECT *, " +
                "mr.mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "ORDER BY f.rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        String sql = "SELECT *, " +
                "mr.mpa_name, " +
                "fr.genre_id " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_genre fr ON f.film_id = fr.film_id " +
                "WHERE genre_id = ? AND YEAR(film_releaseDate) = ? " +
                "ORDER BY f.rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, year, count);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(Integer count, Integer genreId) {
        String sql = "SELECT *, " +
                "mr.mpa_name, " +
                "fr.genre_id " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_genre fr ON f.film_id = fr.film_id " +
                "WHERE genre_id = ? " +
                "ORDER BY f.rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
    }

    @Override
    public List<Film> getPopularFilmsByYear(Integer count, Integer year) {
        String sql = "SELECT *, " +
                "mr.mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "WHERE YEAR(film_releaseDate) = ?" +
                "ORDER BY f.rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
    }

    @Override
    public List<Film> getFilmRecommendations(Long userId) {
        String sql = "WITH film_id_recommend AS " +
                "(SELECT film_id FROM film_marks WHERE user_id IN " +
                "(SELECT fm2.user_id " +
                "FROM film_marks fm1 " +
                "JOIN film_marks fm2 ON fm2.film_id = fm1.film_id AND fm2.user_id != fm1.user_id " +
                "WHERE fm1.user_id = ? AND fm1.mark > 5 AND fm2.mark > 5 " +
                "GROUP BY fm2.user_id " +
                "ORDER BY count(fm2.user_id) DESC " +
                "LIMIT 5) AND mark > 5 " +
                "EXCEPT SELECT film_id FROM film_marks WHERE user_id = ?) " +
                "SELECT films.*, mpa_ratings.mpa_name FROM film_id_recommend " +
                "LEFT JOIN films ON film_id_recommend.film_id = films.film_id " +
                "LEFT JOIN mpa_ratings ON films.mpa_id = mpa_ratings.mpa_id " +
                "ORDER BY films.rate";

        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, userId);
    }


    @Override
    public List<Film> getUsersCommonFilms(Long userId, Long otherUserId) {
        String sql = "SELECT *, " +
                "mr.mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "WHERE f.film_id IN " +
                "(SELECT film_id FROM (SELECT film_id FROM " +
                "film_marks WHERE user_id = ? AND mark > 5 " +
                "INTERSECT SELECT film_id FROM film_marks " +
                "WHERE user_id = ? AND mark > 5)) "+
                "ORDER BY rate";

        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, otherUserId);
    }

    @Override
    public void deleteFilmById(Long filmId) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Film> searchByTitleOnly(String query) {
        final String sql =
                "SELECT f.film_id, f.film_name, f.film_description, f.film_releasedate, f.film_duration, " +
                "f.mpa_id, mr.mpa_name, f.rate " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "WHERE LOWER(f.film_name) LIKE LOWER(?) " +
                "ORDER BY f.rate DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, "%" + query + "%");
    }

    @Override
    public List<Film> searchByDirectorOnly(String query) {
        final String sql = "SELECT distinct f.film_id, f.film_name, f.film_description, f.film_releasedate, f.film_duration, " +
                "f.mpa_id, mr.mpa_name, f.rate " +
                "FROM directors d " +
                "LEFT JOIN film_director fd ON d.director_id = fd.director_id " +
                "LEFT JOIN films f ON fd.film_id = f.film_id " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "WHERE LOWER(d.director_name) LIKE LOWER(?) " +
                "ORDER BY f.rate DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, "%" + query + "%");
    }

    @Override
    public List<Film> searchByTitleAndDirector(String query) {
        final String sql =
                "SELECT distinct f.film_id, f.film_name, f.film_description, f.film_releasedate, f.film_duration, " +
                "f.mpa_id, mr.mpa_name, f.rate " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.director_id " +
                "WHERE LOWER(f.film_name) LIKE LOWER(?) " +
                "OR LOWER(d.director_name) LIKE LOWER(?) " +
                "ORDER BY f.rate DESC";
        final String queryString = "%" + query + "%";
        return jdbcTemplate.query(sql, this::mapRowToFilm, queryString, queryString);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("film_description"))
                .releaseDate(rs.getDate("film_releaseDate").toLocalDate())
                .rate(rs.getFloat("rate"))
                .duration(rs.getInt("film_duration"))
                .mpa(new Mpa(
                        rs.getInt("mpa_id"),
                        rs.getString("mpa_name")))
                .genres(new LinkedHashSet<>())
                .directors(new HashSet<>())
                .build();
    }
}
package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
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
    public List<Film> getPopularFilms(int count, Integer... genreAndYear) {
        String sql = "SELECT films.*, mpa.mpa_name FROM films LEFT JOIN film_genre AS genres ON films.film_id = genres.film_id " +
                "LEFT JOIN mpa_ratings AS mpa ON films.mpa_id = mpa.mpa_id " +
                "LEFT JOIN film_likes AS likes ON films.film_id = likes.film_id";



        if (genreAndYear[0] != null) {
            sql += " WHERE genres.genre_id = " + genreAndYear[0];
        }

        if (genreAndYear[1] != null) {
            if (genreAndYear[0] != null) {
                sql += " AND year(film_releaseDate) = " + genreAndYear[1];
            } else {
                sql += " WHERE year(film_releaseDate) = " + genreAndYear[1];
            }
        }

        sql += " GROUP BY films.film_id ORDER BY count(DISTINCT likes.user_id) DESC LIMIT " + count;

        return jdbcTemplate.query(sql, this::mapRowToFilm);
        }

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

    @Override
    public List<Film> getUsersCommonFilms(int userId, int otherUserId) {
        String excIdMsg = "";
        String sql;

        // проверка наличия пользователей по id в БД
        if (!userDbStorage.containsInStorage(userId)) {
            excIdMsg = " first id " + userId + ". ";
        }
        if (!userDbStorage.containsInStorage(otherUserId)) {
            excIdMsg += " second id " + otherUserId + ". ";
        }

        // если хотя бы один пользователь не найден выбросить исключение
        if (excIdMsg.length() > 0) {
            throw new NotFoundException("User with" + excIdMsg + " not found");
        }

        sql = String.format("WITH common_users_films AS (SELECT likes.film_id FROM film_likes AS likes " +
                "WHERE likes.user_id = %s INTERSECT SELECT likes.film_id FROM film_likes AS likes " +
                "WHERE likes.user_id = %s), top_films AS (SELECT films.* FROM films " +
                "LEFT JOIN film_likes AS likes ON likes.film_id = films.film_id " +
                "GROUP BY films.film_id ORDER BY COUNT(likes.film_id) DESC ) " +
                "SELECT top_films.*, mpa.mpa_name FROM top_films " +
                "LEFT JOIN mpa_ratings mpa ON top_films.mpa_id = mpa.mpa_id " +
                "LEFT JOIN common_users_films ON common_users_films.film_id = top_films.film_id " +
                "WHERE top_films.film_id IN (common_users_films.film_id)", userId, otherUserId);

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public void deleteFilmById(int filmId) {
        // проверка наличия фильма по id в БД, если не найден выбросить исключение
        if (!containsInStorage(filmId)) {
            throw new NotFoundException("Film with id " + filmId + " not found");
        }

        String sql = String.format("DELETE FROM films WHERE film_id = '%s'", filmId);
        jdbcTemplate.update(sql);
    }

    @Override
    public List<Film> getFilmRecommendations(int userId) {
        // проверка наличия пользователя по id в БД если не найден выбросить исключение
        if (!userDbStorage.containsInStorage(userId))  {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        String sql = "WITH film_id_recommend AS (SELECT film_id FROM film_likes WHERE user_id = " +
                "(WITH likes_count AS (SELECT user_id, COUNT(film_id) AS l_count FROM film_likes GROUP BY user_id), " +
                "common_likes AS (SELECT * FROM film_likes WHERE film_id IN (SELECT film_id FROM film_likes " +
                "WHERE user_id = " + userId + " INTERSECT SELECT film_id FROM film_likes WHERE user_id <> " + userId + ")) " +
                "SELECT common_likes.user_id FROM common_likes LEFT JOIN likes_count ON likes_count.user_id = common_likes.user_id " +
                "GROUP BY common_likes.user_id HAVING MAX(common_likes.user_id <> " + userId + ") " +
                "AND l_count > (SELECT COUNT(*) FROM common_likes WHERE user_id = " + userId + ")) " +
                "EXCEPT SELECT film_id FROM film_likes WHERE user_id = " + userId + ") " +
                "SELECT films.*, mpa_ratings.mpa_name FROM film_id_recommend " +
                "LEFT JOIN films ON film_id_recommend.film_id = films.film_id " +
                "LEFT JOIN mpa_ratings ON films.mpa_id = mpa_ratings.mpa_id ";

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }
}
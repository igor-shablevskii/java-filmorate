package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
public class UserDbStorage implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (user_name, user_login, user_email, user_birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET user_name = ?, user_login = ?, user_email = ?, user_birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getLogin()
                , user.getEmail()
                , user.getBirthday()
                , user.getId());
        return user;
    }

    @Override
    public boolean delete(User user) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT user_id, user_name, user_login, user_email, user_birthday FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(Long userId) {
        String sqlQuery = "SELECT user_id, user_name, user_login, user_email, user_birthday " +
                "FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public boolean containsInStorage(Long userId) {
        String sqlQuery = "SELECT count(*) FROM users WHERE user_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId);
        return result == 1;
    }

    @Override
    public void deleteUserById(Long userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .name(resultSet.getString("user_name"))
                .login(resultSet.getString("user_login"))
                .email(resultSet.getString("user_email"))
                .birthday(resultSet.getDate("user_birthday").toLocalDate())
                .build();
    }
}
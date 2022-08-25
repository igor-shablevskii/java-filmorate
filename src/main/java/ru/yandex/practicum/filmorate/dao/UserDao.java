package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {

    /**
     * Сохранить пользователя в БД
     */
    User save(User user);

    /**
     * Удалить пользователя из БД
     */
    boolean delete(User user);

    /**
     * Обновить пользователя в БД
     */
    User update(User user);

    /**
     * Проверка наличия пользователя в БД
     */
    boolean containsInStorage(Long userId);

    /**
     * Получить всех пользователей
     */
    List<User> getAllUsers();

    /**
     * Получить пользователя по id
     */
    User getUserById(Long userId);

    /**
     * Удалить пользователя по id
     */
    void deleteUserById(Long userId);
}
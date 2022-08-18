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
    boolean containsInStorage(int userId);

    /**
     * Получить всех пользователей
     */
    List<User> getAllUsers();

    /**
     * Получить пользователя по id
     */
    User getUserById(int userId);

    // Метод удаляет пользователя по id
    public void deleteUserById(int userId);
}
package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendDao {

    /**
     * Добавить пользователю с id == userId
     * друга с id == friendId
     */
    void saveFriend(int userId, int friendId);

    /**
     * Удалить у пользователя с id == userId
     * друга с id == friendId
     */
    void deleteFriend(int userId, int friendId);

    /**
     * Получить все друзей пользователя
     */
    List<Integer> getFriendsByUserId(int userId);

    /**
     * Проверка наличия записи в БД
     */
    boolean containsInStorage(int filmId, int friendId);
}

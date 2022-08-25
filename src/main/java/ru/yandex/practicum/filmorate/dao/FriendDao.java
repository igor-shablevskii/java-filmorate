package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendDao {

    /**
     * Добавить пользователю с id == userId
     * друга с id == friendId
     */
    void saveFriend(Long userId, Long friendId);

    /**
     * Удалить у пользователя с id == userId
     * друга с id == friendId
     */
    void deleteFriend(Long userId, Long friendId);

    /**
     * Получить все друзей пользователя
     */
    List<Long> getFriendsByUserId(Long userId);

    /**
     * Проверка наличия записи в БД
     */
    boolean containsInStorage(Long filmId, Long friendId);
}

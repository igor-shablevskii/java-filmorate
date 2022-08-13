package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface LikeDao {

    /**
     * Записать лайк в БД
     * к фильму с id == filmId
     * от пользователя с id == userId
     */
    void saveLike(int filmId, int userId);

    /**
     * Удалить лайк из БД
     * к фильму с id == filmId
     * от пользователя с id == userId
     */
    void deleteLike(int filmId, int userId);

    /**
     * Получить количество лайков
     * к фильму по его id
     */
    Integer getCountLikesByFilmId(int filmId);

    /**
     * Получить список id пользователей
     * поставивших лайк к фильму по его id
     */
    List<Integer> getAllUsersLikeFilm(int film_id);

    /**
     * Проверка наличия лайка в БД
     * у фильма с id == filmId
     * от пользователя с id == userId
     */
    boolean containsInStorage(int filmId, int userId);
}

package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface LikeDao {

    /**
     * Записать лайк в БД
     * к фильму с id == filmId
     * от пользователя с id == userId
     */
    void saveLike(Long filmId, Long userId);

    /**
     * Удалить лайк из БД
     * к фильму с id == filmId
     * от пользователя с id == userId
     */
    void deleteLike(Long filmId, Long userId);

    /**
     * Получить количество лайков
     * к фильму по его id
     */
    Integer getCountLikesByFilmId(Long filmId);

    /**
     * Получить список id пользователей
     * поставивших лайк к фильму по его id
     */
    List<Integer> getAllUsersLikeFilm(Long film_id);

    /**
     * Проверка наличия лайка в БД
     * у фильма с id == filmId
     * от пользователя с id == userId
     */
    boolean containsInStorage(Long filmId, Long userId);
}

package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDao {


    /**
     * Возвращает список событий
     * для пользователя по его id
     */
    List<Feed> getAllFeedsByUserId(int id);

    /**
     * Добавить событие в БД
     */
    void create(Feed feed);

    /**
     * Проверить наличие события в БД
     * по его id
     */
    boolean containsInStorage(int id);
}

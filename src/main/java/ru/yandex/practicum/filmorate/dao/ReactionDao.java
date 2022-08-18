package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Reaction;

import java.util.List;

public interface ReactionDao {
    /**
     * Получить список всех реакций для отзыва
     */
    List<Reaction> getReactions(Integer reviewId);

    /**
     * Добавить лайк пользователя отзыву
     */
    void saveLike(int reviewId, int userId);

    /**
     * Добавить дизлайк пользователя отзыву
     */
    void saveDislike(int reviewId, int userId);

    /**
     * Удалить лайк/дизлайк пользователя отзыву
     */
    void deleteReaction(int reviewId, int userId);

    /**
     * Проверка наличия лайка/дизлайка в БД
     */
    boolean containsReactionInStorage(int reviewId, int userId);

}

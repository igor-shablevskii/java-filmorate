package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Reaction;

import java.util.List;

public interface ReactionDao {
    /**
     * Получить список всех реакций для отзыва
     */
    List<Reaction> getReactions(Long reviewId);

    /**
     * Добавить лайк пользователя отзыву
     */
    void saveLike(Long reviewId, Long userId);

    /**
     * Добавить дизлайк пользователя отзыву
     */
    void saveDislike(Long reviewId, Long userId);

    /**
     * Удалить лайк/дизлайк пользователя отзыву
     */
    void deleteReaction(Long reviewId, Long userId);

    /**
     * Проверка наличия лайка/дизлайка в БД
     */
    boolean containsReactionInStorage(Long reviewId, Long userId);

}

package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {
    /**
     * Сохранить отзыв в БД
     */
    Review save(Review review);

    /**
     * Обновление отзыва в БД
     */
    Review update(Review review);

    /**
     * Получить отзывы из БД в указанном количестве,
     * либо 10 если количество не указано
     */
    List<Review> getReviews(Integer count);

    /**
     * Получить отзывы к фильму из БД в указанном количестве,
     * либо 10 если количество не указано
     */
    List<Review> getReviewsByFilmId(Integer count, Long filmId);

    /**
     * Получить отзыв по его id из БД
     */
    Review getReviewById(Long reviewId);

    /**
     * Удалить отзыв по его id из БД
     */
    void deleteReview(Long reviewId);

    /**
     * Проверка наличия отзыва в БД
     */
    boolean containsInStorage(Long reviewId);

}


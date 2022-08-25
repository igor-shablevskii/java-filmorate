package ru.yandex.practicum.filmorate.dao;

public interface MarkDao {

    /**
     * Сохранить оценку
     */
    void save(Long filmId, Long userId, Byte mark);

    /**
     * Удалить оценку
     */
    void remove(Long filmId, Long userId);

    /**
     * Обновить оценку
     */
    void update(Long filmId, Long userId, Byte mark);

    /**
     * Проверить наличие оценки в БД
     */
    boolean containsInStorage(Long filmId, Long userId);
}

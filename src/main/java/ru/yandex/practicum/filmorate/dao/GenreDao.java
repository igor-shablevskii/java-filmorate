package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {

    /**
     * Записать все жанры к фильму в БД
     */
    void setGenre(Film film);

    /**
     * Получить все жанры фильма по его id
     */
    List<Genre> loadGenres(Long filmId);

    /**
     * Получить все жанры
     * записанные в справочнике БД
     */
    List<Genre> getAllGenres();

    /**
     * Получить жарн по его id
     */
    Genre getGenreById(Integer id);

    /**
     * Проверить наличие жанра в справочнике БД
     * по его id
     */
    boolean containsInStorage(Integer id);
}

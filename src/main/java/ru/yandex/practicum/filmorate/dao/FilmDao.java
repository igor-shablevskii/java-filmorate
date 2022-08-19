package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {

    /**
     * Сохранить фильм в БД
     */
    Film save(Film film);

    /**
     * Обновление фильма в БД
     */
    Film update(Film film);

    /**
     * Получить все фильмы из БД
     */
    List<Film> getAllFilms();

    /**
     * Получить фильм по его id из БД
     */
    Film getFilmById(int filmId);

    /**
     * Получить список фильмов
     * отсортированных по количеству лайков
     * пользователей по desc
     */
    List<Film> getPopularFilms(Integer count);

    /**
     * Проверка наличия фильма в БД
     */
    boolean containsInStorage(int filmId);

    /**
     * Получить список общих фильмов двух пользователей
     * отсортированный по популярности
     */

    List<Film> getUsersCommonFilms(int userId, int otherUserId);

    /**
     * Удалить фильм по id
     */
    void deleteFilmById(int filmId);

    /**
     * Получить список фильмов режиссёра
     * отсортированных по типу sortBy
     */
    List<Film> getSortedFilmsByDirectors(int directorId, String sortBy);

    /**
     * Получить список фильмов
     * отсортированных по количеству лайков
     * пользователей по desc,
     * фильтрованных по жанру и году
     */
    List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year);

    /**
     * Получить список фильмов
     * отсортированных по количеству лайков
     * пользователей по desc,
     * фильтрованных по жанру
     */
    List<Film> getPopularFilmsByGenre(Integer count, Integer genreId);

    /**
     * Получить список фильмов
     * отсортированных по количеству лайков
     * пользователей по desc,
     * фильтрованных по году
     */
    List<Film> getPopularFilmsByYear(Integer count, Integer year);

}

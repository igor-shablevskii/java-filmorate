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
    Film getFilmById(Long filmId);

    /**
     * Получить список фильмов
     * отсортированных по количеству лайков
     * пользователей по desc
     */
    List<Film> getPopularFilms(Integer count);

    /**
     * Проверка наличия фильма в БД
     */
    boolean containsInStorage(Long filmId);

    /**
     * Получить список общих фильмов двух пользователей
     * отсортированный по популярности
     */
    List<Film> getUsersCommonFilms(Long userId, Long otherUserId);

    /**
     * Удалить фильм по id
     */
    void deleteFilmById(Long filmId);

    /**
     * Получить список фильмов режиссёра
     * отсортированных по типу sortBy
     */
    List<Film> getSortedFilmsByDirectors(Long directorId);

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

    /**
     * Получить список рекомендованных
     * для пользователя фильмов
     */
    List<Film> getFilmRecommendations(Long userId);

    /**
     * Получить список фильмов,
     * отсортированных по популярности,
     * содержащих в названии поисковый запрос
     */
    List<Film> searchByTitleOnly(String query);


    /**
     * Получить список фильмов,
     * отсортированных по популярности,
     * содержащих в поле режиссер поисковый запрос
     */
    List<Film> searchByDirectorOnly(String query);

    /**
     * Получить список фильмов,
     * отсортированных по популярности,
     * содержащих в названии или в поле режиссер поисковый запрос
     */
    List<Film> searchByTitleAndDirector(String query);


}

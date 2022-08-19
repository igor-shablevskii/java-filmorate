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
    List<Film> getPopularFilms(int count, Integer ... genreAndYear);

    /**
     * Проверка наличия фильма в БД
     */
    boolean containsInStorage(int filmId);

    // Метод возвращает из БД список общих фильмов по id двух пользователей с сортировкой по популярности
    List<Film> getUsersCommonFilms(int userId, int otherUserId);

    // Метод удаляет фильм по id
    void deleteFilmById(int filmId);

    /**
     * Получить список фильмов режиссёра
     * отсортированных по типу sortBy
     */
    List<Film> getSortedFilmsByDirectors(int directorId, String sortBy);

/*    Метод возвращает список рекомендуемых фильмов по id пользователя, которым он не ставил лайки
    через поиск максимального количества пересечений в лайках по остальным фильмам у другого пользователя*/
    List<Film> getFilmRecommendations(int userId);
}

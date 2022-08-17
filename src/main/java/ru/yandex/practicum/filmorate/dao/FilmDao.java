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
    List<Film> getPopularFilms(int count);

    /**
     * Проверка наличия фильма в БД
     */
    boolean containsInStorage(int filmId);

    // Метод возвращает из БД список общих фильмов по id двух пользователей с сортировкой по популярности
    List<Film> getUsersCommonFilms(int userId, int otherUserId);

    // Метод удаляет фильм по id
    public void deleteFilmById(int filmId);
}

package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorDao {

    /**
     * Записать всех режиссёров к фильму в БД
     * (использется в FilmService при
     * сохранении и обновлении фильма)
     */
    void setDirector(Film savedFilm);

    /**
     * Получить всех режиссёров фильма по его id
     * (используется в FilmService
     * при получении фильма или списка фильмов из БД)
     */
    List<Director> loadDirectors(int filmId);

    /**
     * Получить всех режиссёров
     */
    List<Director> getAllDirectors();

    /**
     * Получить режиссёра по его id
     */
    Director getDirectorById(int id);

    /**
     * Проверить наличие режиссёра в БД
     * по его id
     */
    boolean containsInStorage(int id);

    /**
     * Сохранить режиссёра в БД
     */
    Director create(Director director);

    /**
     * Обновить режиссёра
     */
    Director update(Director director);

    /**
     * Удалить режиссёра по id из БД
     */
    void removeDirectorById(int id);
}

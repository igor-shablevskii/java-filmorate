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
    List<Director> loadDirectors(Long filmId);

    /**
     * Получить всех режиссёров
     */
    List<Director> getAllDirectors();

    /**
     * Получить режиссёра по его id
     */
    Director getDirectorById(Long id);

    /**
     * Проверить наличие режиссёра в БД
     * по его id
     */
    boolean containsInStorage(Long id);

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
    void removeDirectorById(Long id);
}

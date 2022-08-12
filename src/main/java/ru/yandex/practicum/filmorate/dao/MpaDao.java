package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {

    /**
     * Получить все MPA рейтинги
     * из справочника
     */
    List<Mpa> getAllMpa();

    /**
     * Получить MPA рейтинг по его id
     * из справочника
     */
    Mpa getMpaById(int id);

    /**
     * Проверка наличия map
     * в справочнике по id
     */
    boolean containsInStorage(int id);
}
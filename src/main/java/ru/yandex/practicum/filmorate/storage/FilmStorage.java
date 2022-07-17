package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film save(Film film);

    void delete(Film film);

    Film update(Film film);

    boolean containsInStorage(int filmId);

    List<Film> getAllFilms();

    Film getFilmById(int filmId);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Integer> getLikesByFilmId(int filmId);

    Map<Integer, HashSet<Integer>> getAllLikes();
}
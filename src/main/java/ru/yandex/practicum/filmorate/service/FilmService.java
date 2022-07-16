package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {

    @Autowired
    FilmStorage filmStorage;
    @Autowired
    UserStorage userStorage;

    public Film create(Film film) {
        return filmStorage.save(film);
    }

    public Film update(Film film) {
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Film with id = " + film.getId() + " not found");
        }
        return filmStorage.update(film);
    }

    public Film getFilmById(int filmId) {
        final Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Film with id = " + filmId + " not found");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) {
        if (!filmStorage.containsInStorage(filmId)) {
            throw new NotFoundException("Film with id = " + filmId + " not found");
        }
        if (!userStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmStorage.containsInStorage(filmId)) {
            throw new NotFoundException("Film with id = " + filmId + " not found");
        }
        if (!userStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllLikes()
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(o -> -o.getValue().size()))
                .limit(count)
                .map(Map.Entry::getKey)
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }
}
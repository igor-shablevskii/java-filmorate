package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private final HashMap<Integer, HashSet<Integer>> likes = new HashMap<>();
    private Integer id = 0;

    @Override
    public Film save(Film film) {
        film.setId(++id);
        likes.put(id, new HashSet<>());
        films.put(id, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(Film film) {
        likes.remove(film.getId());
        films.remove(film.getId());
    }

    @Override
    public Film getFilmById(int filmId) {
        return films.get(filmId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        likes.get(filmId).add(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        likes.get(filmId).remove(userId);
    }

    @Override
    public boolean containsInStorage(int filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Integer> getLikesByFilmId(int filmId) {
        return new ArrayList<>(likes.get(filmId));
    }

    @Override
    public HashMap<Integer, HashSet<Integer>> getAllLikes() {
        return likes;
    }
}
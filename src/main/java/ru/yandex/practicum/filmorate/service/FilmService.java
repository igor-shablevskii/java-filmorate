package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmDao filmDbStorage;
    private final UserDao userDbStorage;
    private final GenreDao genreDbStorage;
    private final LikeDao likeDbStorage;
    private final DirectorDao directorDbStorage;

    @Autowired
    public FilmService(FilmDao filmDbStorage,
                       UserDao userDbStorage,
                       GenreDao genreDbStorage,
                       LikeDao likeDbStorage,
                       DirectorDao directorDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.directorDbStorage = directorDbStorage;
    }

    public Film create(Film film) {
        Film savedFilm = filmDbStorage.save(film);
        genreDbStorage.setGenre(savedFilm);
        directorDbStorage.setDirector(savedFilm);
        return savedFilm;
    }

    public Film update(Film film) {
        if (!filmDbStorage.containsInStorage(film.getId())) {
            throw new NotFoundException("Film with id = " + film.getId() + " not found");
        }
        Film updatedFilm = filmDbStorage.update(film);
        genreDbStorage.setGenre(updatedFilm);
        directorDbStorage.setDirector(updatedFilm);
        return updatedFilm;
    }

    public Film getFilmById(int filmId) {
        if (!filmDbStorage.containsInStorage(filmId)) {
            throw new NotFoundException("Film with id = " + filmId + " not found");
        }
        Film film = filmDbStorage.getFilmById(filmId);
        film.getGenres().addAll(genreDbStorage.loadGenres(filmId));
        film.getDirectors().addAll(directorDbStorage.loadDirectors(filmId));
        return film;
    }

    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms()
                .stream()
                .peek(f -> f.getGenres().addAll(genreDbStorage.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDbStorage.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }

    public void saveLike(int filmId, int userId) {
        if (!filmDbStorage.containsInStorage(filmId)) {
            throw new NotFoundException("Film with id = " + filmId + " not found");
        }
        if (!userDbStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        likeDbStorage.saveLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmDbStorage.containsInStorage(filmId)) {
            throw new NotFoundException("Film with id = " + filmId + " not found");
        }
        if (!userDbStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        likeDbStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmDbStorage.getPopularFilms(count)
                .stream()
                .peek(f -> f.getGenres().addAll(genreDbStorage.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDbStorage.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }

    public List<Film> getSortedFilmsByDirectors(int directorId, String sortBy) {
        if (!directorDbStorage.containsInStorage(directorId)) {
            throw new NotFoundException("Director with id = " + directorId + " not found");
        }
        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new RuntimeException("Sorting type not found");
        }
        return filmDbStorage.getSortedFilmsByDirectors(directorId, sortBy)
                .stream()
                .peek(f -> f.getGenres().addAll(genreDbStorage.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDbStorage.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }
}
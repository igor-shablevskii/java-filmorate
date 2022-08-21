package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmDao filmDbStorage;
    private final UserDao userDbStorage;
    private final GenreDao genreDbStorage;
    private final LikeDao likeDbStorage;
    private final DirectorDao directorDbStorage;
    private final FeedDao feedDbStorage;

    @Autowired
    public FilmService(FilmDao filmDbStorage,
                       UserDao userDbStorage,
                       GenreDao genreDbStorage,
                       LikeDao likeDbStorage,
                       DirectorDao directorDbStorage,
                       FeedDao feedDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.directorDbStorage = directorDbStorage;
        this.feedDbStorage = feedDbStorage;
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
        System.out.println(updatedFilm.getRate());
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
        feedDbStorage.create(new Feed(userId, "LIKE", "ADD", filmId));
        Film film = getFilmById(filmId);
        film.setRate(film.getRate() + 1);
        update(film);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmDbStorage.containsInStorage(filmId)) {
            throw new NotFoundException("Film with id = " + filmId + " not found");
        }
        if (!userDbStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        likeDbStorage.deleteLike(filmId, userId);
        feedDbStorage.create(new Feed(userId, "LIKE", "REMOVE", filmId));
        Film film = getFilmById(filmId);
        film.setRate(film.getRate() - 1);
        update(film);
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer yearId) {
        List<Film> popularFilms;
        if (genreId != null && yearId != null) {
            popularFilms = filmDbStorage.getPopularFilmsByGenreAndYear(count, genreId, yearId);
        } else if (genreId != null) {
            popularFilms = filmDbStorage.getPopularFilmsByGenre(count, genreId);
        } else if (yearId != null) {
            popularFilms = filmDbStorage.getPopularFilmsByYear(count, yearId);
        } else {
            popularFilms = filmDbStorage.getPopularFilms(count);
        }
        return popularFilms
                .stream()
                .peek(f -> f.getGenres().addAll(genreDbStorage.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDbStorage.loadDirectors(f.getId())))
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
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

    public List<Film> getUsersCommonFilms(int userId, int otherUserId) {
        return filmDbStorage.getUsersCommonFilms(userId, otherUserId);
    }

    public void deleteFilmById(int filmId) {
        filmDbStorage.deleteFilmById(filmId);
    }

    public List<Film> getFilmRecommendations(Integer userId) {
        return filmDbStorage.getFilmRecommendations(userId).stream()
                .peek(f -> f.getGenres().addAll(genreDbStorage.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDbStorage.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }

    public List<Film> search(String query, String by) {
        final String TITLE = "title";
        final String DIRECTOR = "director";

        if (by == null || (!by.contains(TITLE) && !by.contains(DIRECTOR))) {
            throw new ValidationException("There isn't type for search. Use by=director,title");
        }
        List<String> parameters = Arrays.asList(by.split(","));
        List<Film> films = new ArrayList<>();
        if (parameters.size() == 2 && parameters.contains(DIRECTOR) && parameters.contains(TITLE)) {
            films.addAll(filmDbStorage.searchByTitleAndDirector(query));
        } else if (parameters.size() == 1 && parameters.contains(DIRECTOR)) {
            films.addAll(filmDbStorage.searchByDirectorOnly(query));
        } else if (parameters.size() == 1 && parameters.contains(TITLE)) {
            films.addAll(filmDbStorage.searchByTitleOnly(query));
        } else {
            throw new ValidationException("There isn't type for search. Use by=director,title");
        }

        return films.stream()
                .peek(f -> f.getGenres().addAll(genreDbStorage.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDbStorage.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }

}
package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmDao filmDao;
    private final UserDao userDao;
    private final GenreDao genreDao;
    private final LikeDao likeDao;
    private final DirectorDao directorDao;
    private final FeedDao feedDao;

    @Autowired
    public FilmService(FilmDao filmDao,
                       UserDao userDao,
                       GenreDao genreDao,
                       LikeDao likeDao,
                       DirectorDao directorDao,
                       FeedDao feedDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
        this.likeDao = likeDao;
        this.directorDao = directorDao;
        this.feedDao = feedDao;
    }

    public Film create(Film film) {
        Film savedFilm = filmDao.save(film);
        genreDao.setGenre(savedFilm);
        directorDao.setDirector(savedFilm);
        return savedFilm;
    }

    public Film update(Film film) {
        isFilmExists(film.getId());
        Film updatedFilm = filmDao.update(film);
        genreDao.setGenre(updatedFilm);
        directorDao.setDirector(updatedFilm);
        return updatedFilm;
    }

    public Film getFilmById(int filmId) {
        isFilmExists(filmId);
        Film film = filmDao.getFilmById(filmId);
        film.getGenres().addAll(genreDao.loadGenres(filmId));
        film.getDirectors().addAll(directorDao.loadDirectors(filmId));
        return film;
    }

    public List<Film> getAllFilms() {
        return filmDao.getAllFilms()
                .stream()
                .peek(f -> f.getGenres().addAll(genreDao.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDao.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }

    public void saveLike(int filmId, int userId) {
        isFilmExists(filmId);
        isUserExists(userId);
        likeDao.saveLike(filmId, userId);
        feedDao.create(new Feed(userId, EventType.LIKE, Operation.ADD, filmId));
    }

    public void deleteLike(int filmId, int userId) {
        isFilmExists(filmId);
        isUserExists(userId);
        likeDao.deleteLike(filmId, userId);
        feedDao.create(new Feed(userId, EventType.LIKE, Operation.REMOVE, filmId));
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer yearId) {
        List<Film> popularFilms;
        if (genreId != null && yearId != null) {
            popularFilms = filmDao.getPopularFilmsByGenreAndYear(count, genreId, yearId);
        } else if (genreId != null) {
            popularFilms = filmDao.getPopularFilmsByGenre(count, genreId);
        } else if (yearId != null) {
            popularFilms = filmDao.getPopularFilmsByYear(count, yearId);
        } else {
            popularFilms = filmDao.getPopularFilms(count);
        }
        return popularFilms
                .stream()
                .peek(f -> f.getGenres().addAll(genreDao.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDao.loadDirectors(f.getId())))
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .collect(Collectors.toList());
    }

    public List<Film> getSortedFilmsByDirectors(int directorId, String sortBy) {
        isDirectorExists(directorId);
        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new RuntimeException("Sorting type not found");
        }
        List<Film> filmList = filmDao.getSortedFilmsByDirectors(directorId, sortBy);
        if (sortBy.equals("year")) {
            return filmList.stream().sorted(Comparator.comparing(Film::getReleaseDate))
                    .peek(f -> f.getGenres().addAll(genreDao.loadGenres(f.getId())))
                    .peek(f -> f.getDirectors().addAll(directorDao.loadDirectors(f.getId())))
                    .collect(Collectors.toList());
        }
        return filmList.stream()
                .peek(f -> f.getGenres().addAll(genreDao.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDao.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }

    public List<Film> getUsersCommonFilms(int userId, int otherUserId) {
        isUserExists(userId);
        isUserExists(otherUserId);
        return filmDao.getUsersCommonFilms(userId, otherUserId);
    }

    public void deleteFilmById(int filmId) {
        isFilmExists(filmId);
        filmDao.deleteFilmById(filmId);
    }

    public List<Film> getFilmRecommendations(Integer userId) {
        return filmDao.getFilmRecommendations(userId).stream()
                .peek(f -> f.getGenres().addAll(genreDao.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDao.loadDirectors(f.getId())))
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
            films.addAll(filmDao.searchByTitleAndDirector(query));
        } else if (parameters.size() == 1 && parameters.contains(DIRECTOR)) {
            films.addAll(filmDao.searchByDirectorOnly(query));
        } else if (parameters.size() == 1 && parameters.contains(TITLE)) {
            films.addAll(filmDao.searchByTitleOnly(query));
        } else {
            throw new ValidationException("There isn't type for search. Use by=director,title");
        }
        return films.stream()
                .peek(f -> f.getGenres().addAll(genreDao.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDao.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }

    private void isFilmExists(Integer filmId) {
        if (!filmDao.containsInStorage(filmId)) {
            throw new NotFoundException(String.format("Film with id = %d not found", filmId));
        }
    }

    private void isUserExists(Integer userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }

    private void isDirectorExists(Integer directorId) {
        if (!directorDao.containsInStorage(directorId)) {
            throw new NotFoundException(String.format("Director with id = %d not found", directorId));
        }
    }
}
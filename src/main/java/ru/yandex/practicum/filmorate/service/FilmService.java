package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;
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

    public Film getFilmById(Long filmId) {
        isFilmExists(filmId);
        Film film = filmDao.getFilmById(filmId);
        film.getGenres().addAll(genreDao.loadGenres(filmId));
        film.getDirectors().addAll(directorDao.loadDirectors(filmId));
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmDao.getAllFilms();
        return addGenresAndDirectors(films);
    }

    public void saveLike(Long filmId, Long userId) {
        isFilmExists(filmId);
        isUserExists(userId);
        feedDao.create(new Feed(userId, EventType.LIKE, Operation.ADD, filmId));
        if (likeDao.containsInStorage(filmId, userId)) {
            return;
        }
        likeDao.saveLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
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
        return addGenresAndDirectors(popularFilms)
                .stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .collect(Collectors.toList());
    }

    public List<Film> getSortedFilmsByDirectors(Long directorId, FilmSortBy sortBy) {
        isDirectorExists(directorId);
        List<Film> films = filmDao.getSortedFilmsByDirectors(directorId);
        if (sortBy.equals(FilmSortBy.YEAR)) {
            return addGenresAndDirectors(films)
                    .stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .collect(Collectors.toList());
        }
        return addGenresAndDirectors(films);
    }

    public List<Film> getUsersCommonFilms(Long userId, Long otherUserId) {
        isUserExists(userId);
        isUserExists(otherUserId);
        return filmDao.getUsersCommonFilms(userId, otherUserId);
    }

    public void deleteFilmById(Long filmId) {
        isFilmExists(filmId);
        filmDao.deleteFilmById(filmId);
    }

    public List<Film> getFilmRecommendations(Long userId) {
        List<Film> films = filmDao.getFilmRecommendations(userId);
        return addGenresAndDirectors(films);
    }

    public List<Film> search(String query, String by) {
        List<Film> films = new ArrayList<>();
        Set<FilmSearchBy> setParameters = Arrays.stream(by.split(","))
                .map(String::toUpperCase)
                .map(FilmSearchBy::valueOf)
                .collect(Collectors.toSet());
        if (setParameters.contains(FilmSearchBy.TITLE) && setParameters.contains(FilmSearchBy.DIRECTOR)) {
            films.addAll(filmDao.searchByTitleAndDirector(query));
        } else if (setParameters.contains(FilmSearchBy.TITLE)) {
            films.addAll(filmDao.searchByTitleOnly(query));
        } else {
            films.addAll(filmDao.searchByDirectorOnly(query));
        }
        return addGenresAndDirectors(films);
    }

    private void isFilmExists(Long filmId) {
        if (!filmDao.containsInStorage(filmId)) {
            throw new NotFoundException(String.format("Film with id = %d not found", filmId));
        }
    }

    private void isUserExists(Long userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }

    private void isDirectorExists(Long directorId) {
        if (!directorDao.containsInStorage(directorId)) {
            throw new NotFoundException(String.format("Director with id = %d not found", directorId));
        }
    }

    private List<Film> addGenresAndDirectors(List<Film> films) {
        return films.stream()
                .peek(f -> f.getGenres().addAll(genreDao.loadGenres(f.getId())))
                .peek(f -> f.getDirectors().addAll(directorDao.loadDirectors(f.getId())))
                .collect(Collectors.toList());
    }
}
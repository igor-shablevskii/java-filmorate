package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private final MarkDao markDao;

    @Autowired
    public FilmService(FilmDao filmDao,
                       UserDao userDao,
                       GenreDao genreDao,
                       LikeDao likeDao,
                       DirectorDao directorDao,
                       FeedDao feedDao, MarkDao markDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
        this.likeDao = likeDao;
        this.directorDao = directorDao;
        this.feedDao = feedDao;
        this.markDao = markDao;
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
        directorDao.setDirector(film);
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

    public void saveMark(Long filmId, Long userId, Byte mark) {
        isFilmExists(filmId);
        isUserExists(userId);
        feedDao.create(new Feed(userId, EventType.MARK, Operation.ADD, filmId));
        if (markDao.containsInStorage(filmId, userId)) {
            return;
        }
        markDao.save(filmId, userId, mark);
    }

    public void updateMark(Long filmId, Long userId, Byte mark) {
        isFilmExists(filmId);
        isUserExists(userId);
        feedDao.create(new Feed(userId, EventType.MARK, Operation.UPDATE, filmId));
        markDao.update(filmId, userId, mark);
    }

    public void deleteMark(Long filmId, Long userId) {
        isFilmExists(filmId);
        isUserExists(userId);
        markDao.remove(filmId, userId);
        feedDao.create(new Feed(userId, EventType.MARK, Operation.REMOVE, filmId));
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
                .sorted(Comparator.comparingDouble(Film::getRate).reversed())
                .collect(Collectors.toList());
    }

    public List<Film> getSortedDirectorsFilms(Long directorId, FilmSortBy sortBy) {
        isDirectorExists(directorId);
        List<Film> films;
        if (sortBy.equals(FilmSortBy.YEAR)) {
            films = filmDao.getSortedDirectorsFilmsByYears(directorId);
        } else if (sortBy.equals(FilmSortBy.MARKS)) {
            films = filmDao.getSortedDirectorsFilmsByMarks(directorId);
        } else {
            films = filmDao.getSortedDirectorsFilmsByLikes(directorId);
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
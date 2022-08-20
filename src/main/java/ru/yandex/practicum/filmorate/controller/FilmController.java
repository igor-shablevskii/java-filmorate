package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        validate(film);
        Film createdFilm = filmService.create(film);
        log.info("Film {} created and added in storage", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        Film updatedFilm = filmService.update(film);
        log.info("Film {} updated and saved in storage", updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public List<Film> readAll() {
        List<Film> listFilm = filmService.getAllFilms();
        log.info("Get all films, count = {}", listFilm.size());
        return listFilm;
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        Film film = filmService.getFilmById(filmId);
        log.info("Get film by id = {}", film.getId());
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Added like film id = {} user id = {}", filmId, userId);
        filmService.saveLike(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public void deleteLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Delete like film id = {} user id = {}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        List<Film> popularFilms = filmService.getPopularFilms(count, genreId, year);
        log.info("Get popular films ids = {}", popularFilms.stream().map(Film::getId).collect(Collectors.toList()));
        return popularFilms;
    }


    @GetMapping("/common")
    private List<Film> getUsersCommonFilms(@RequestParam(name = "userId") int userId,
                                           @RequestParam(name = "friendId") int otherUserId) {
        log.info("Get common films by two users id = {}", filmService.getUsersCommonFilms(userId, otherUserId));
        return filmService.getUsersCommonFilms(userId, otherUserId);
    }


    @DeleteMapping("/{filmId}")
    private void deleteFilmById(@PathVariable int filmId) {
        log.info("Delete film by id = {}", filmId);
        filmService.deleteFilmById(filmId);
    }

    @GetMapping(value = "/director/{directorId}")
    public List<Film> getSortedFilmsByDirector(@PathVariable int directorId, @RequestParam("sortBy") String sortBy) {
        return filmService.getSortedFilmsByDirectors(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam(name = "query") String query,
                             @RequestParam(name = "by") String by) {
        log.info("Search films. Query: {}, by: {}", query, by);
        return filmService.search(query, by);
    }

    private void validate(Film film) {
        String message;
        if (film.getName().isEmpty()) {
            message = String.format("У фильма %s отсутствует название", film);
            log.info(message);
            throw new ValidationException(message);
        }
        if (film.getDescription().length() > 200) {
            message = String.format("Описание фильма %s более 200 символов", film.getName());
            log.info(message);
            throw new ValidationException(message);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            message = String.format("У фильма %s некорректная дата релиза", film);
            log.info(message);
            throw new ValidationException(message);
        }
        if (film.getDuration() <= 0) {
            message = String.format("У фильма %s некорректная продолжительность", film);
            log.info(message);
            throw new ValidationException(message);
        }
        if (film.getMpa() == null) {
            message = String.format("У фильма %s отсутствует рейтинг MPA", film);
            log.info(message);
            throw new ValidationException(message);
        }
    }
}
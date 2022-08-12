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
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Get popular films ids = {}", popularFilms.stream().map(Film::getId).collect(Collectors.toList()));
        return popularFilms;
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
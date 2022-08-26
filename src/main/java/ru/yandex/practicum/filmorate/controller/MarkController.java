package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class MarkController {

    private final FilmService filmService;

    @Autowired
    public MarkController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PutMapping("/{filmId}/mark/{userId}/{mark}")
    public void addMark(@PathVariable Long filmId, @PathVariable Long userId, @PathVariable Byte mark) {
        log.info("Add mark. Film id = {}, user id = {}, mark = {}", filmId, userId, mark);
        validateMark(mark);
        filmService.saveMark(filmId, userId, mark);
    }

    @PostMapping("/{filmId}/mark/{userId}/{mark}")
    public void updateMark(@PathVariable Long filmId, @PathVariable Long userId, @PathVariable Byte mark) {
        log.info("Update mark. Film id = {}, user id = {}, mark = {}", filmId, userId, mark);
        validateMark(mark);
        filmService.updateMark(filmId, userId, mark);
    }

    @DeleteMapping("/{filmId}/mark/{userId}")
    public void deleteMark(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Delete mark. Film id = {} user id = {}", filmId, userId);
        filmService.deleteMark(filmId, userId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Added like film id = {} user id = {}", filmId, userId);
        filmService.saveLike(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public void deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Delete like film id = {} user id = {}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    private void validateMark(Byte mark) {
        if (mark < 1 || mark > 10) {
            throw new ValidationException("Оценка должна быть от 1 до 10. Ваша оценка: " + mark);
        }
    }
}
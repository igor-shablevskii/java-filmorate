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

    private void validateMark(byte mark) {
        if (mark < 1 || mark > 10) {
            throw new ValidationException("Оценка должна быть от 1 до 10. Ваша оценка: " + mark);
        }
    }

    @DeleteMapping("/{filmId}/mark/{userId}")
    public void deleteMark(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Delete mark. Film id = {} user id = {}", filmId, userId);
        filmService.deleteMark(filmId, userId);
    }

}
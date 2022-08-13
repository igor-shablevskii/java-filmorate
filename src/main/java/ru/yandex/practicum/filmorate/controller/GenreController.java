package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) {
        Genre genre = genreService.getGenreById(id);
        log.info("Get genre by id = {}", genre.getId());
        return genre;
    }

    @GetMapping
    public List<Genre> readAll() {
        List<Genre> genreList = genreService.getAllGenres();
        log.info("Get all genres, count = {}", genreList.size());
        return genreList;
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Update;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(final DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director create(@RequestBody @Valid Director director) {
        Director createdDirector = directorService.create(director);
        log.info("Director {} created and added in storage", createdDirector);
        return createdDirector;
    }

    @PutMapping
    public Director update(@RequestBody @Validated(Update.class) Director director) {
        Director updatedDirector = directorService.update(director);
        log.info("Director {} updated and saved in storage", updatedDirector);
        return updatedDirector;
    }

    @GetMapping
    public List<Director> getAll() {
        List<Director> directorList = directorService.getAllDirectors();
        log.info("Get all directors, count = {}", directorList.size());
        return directorList;
    }

    @GetMapping(value = "/{id}")
    public Director get(@PathVariable Long id) {
        Director director = directorService.getDirectorById(id);
        log.info("Get director by id = {}", director.getId());
        return director;
    }

    @DeleteMapping(value = "/{id}")
    public void remove(@PathVariable Long id) {
        log.info("Delete director id = {}", id);
        directorService.removeDirectorById(id);
    }
}
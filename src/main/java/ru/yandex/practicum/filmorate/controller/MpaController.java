package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    public Mpa get(@PathVariable int id) {
        Mpa mpa = mpaService.getMpaById(id);
        log.info("Get mpa by id = {}", mpa.getId());
        return mpa;
    }

    @GetMapping
    public List<Mpa> getAll() {
        List<Mpa> mpaList = mpaService.getAllMpa();
        log.info("Get all mpa, count = {}", mpaList.size());
        return mpaList;
    }
}
package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {

    private final MpaDao mpaDbStorage;

    @Autowired
    public MpaService(MpaDao mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa getMpaById(int id) {
        if (!mpaDbStorage.containsInStorage(id)) {
            throw new NotFoundException("MPA with id = " + id + " not found");
        }
        return mpaDbStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }
}
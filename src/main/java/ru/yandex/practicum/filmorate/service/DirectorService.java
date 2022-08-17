package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDao directorDbStorage;

    @Autowired
    public DirectorService(DirectorDao directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public Director create(Director director) {
        return directorDbStorage.create(director);
    }

    public Director update(Director director) {
        if (!directorDbStorage.containsInStorage(director.getId())) {
            throw new NotFoundException("Director with id = " + director.getId() + " not found");
        }
        return directorDbStorage.update(director);
    }

    public List<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        if (!directorDbStorage.containsInStorage(id)) {
            throw new NotFoundException("Director with id = " + id + " not found");
        }
        return directorDbStorage.getDirectorById(id);
    }

    public void removeDirectorById(int id) {
        if (!directorDbStorage.containsInStorage(id)) {
            throw new NotFoundException("Director with id = " + id + " not found");
        }
        directorDbStorage.removeDirectorById(id);
    }
}
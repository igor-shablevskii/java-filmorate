package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Director create(Director director) {
        return directorDao.create(director);
    }

    public Director update(Director director) {
        if (!directorDao.containsInStorage(director.getId())) {
            throw new NotFoundException("Director with id = " + director.getId() + " not found");
        }
        return directorDao.update(director);
    }

    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        if (!directorDao.containsInStorage(id)) {
            throw new NotFoundException("Director with id = " + id + " not found");
        }
        return directorDao.getDirectorById(id);
    }

    public void removeDirectorById(int id) {
        if (!directorDao.containsInStorage(id)) {
            throw new NotFoundException("Director with id = " + id + " not found");
        }
        directorDao.removeDirectorById(id);
    }
}
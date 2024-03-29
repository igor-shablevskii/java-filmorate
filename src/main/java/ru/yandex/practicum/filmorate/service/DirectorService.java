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
        isDirectorExists(director.getId());
        return directorDao.update(director);
    }

    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Director getDirectorById(Long id) {
        isDirectorExists(id);
        return directorDao.getDirectorById(id);
    }

    public void removeDirectorById(Long id) {
        isDirectorExists(id);
        directorDao.removeDirectorById(id);
    }

    private void isDirectorExists(Long id) {
        if (!directorDao.containsInStorage(id)) {
            throw new NotFoundException(String.format("Director with id = %d not found", id));
        }
    }
}
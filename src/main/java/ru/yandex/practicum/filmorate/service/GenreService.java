package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {

    private final GenreDao genreDbStorage;

    @Autowired
    public GenreService(GenreDao genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(Integer id) {
        if (!genreDbStorage.containsInStorage(id)) {
            throw new NotFoundException("Genre with id = " + id + " not found");
        }
        return genreDbStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }
}

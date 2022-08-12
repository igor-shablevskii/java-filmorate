package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.MpaDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikeDao likeDbStorage;

    @Test
    public void filmTest() {
        Film film1 = new Film(0, "Название фильма", "Описание фильма",
                LocalDate.of(1976, 11, 21), 119, new Mpa(1, "G"),
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))));
        Film film2 = new Film(0, "Название другого фильма", "Описание другого фильма",
                LocalDate.of(1998, 2, 3), 59, new Mpa(4, "R"),
                new LinkedHashSet<>(Set.of(new Genre(6, "Боевик"))));
        User user1 = new User(0, "Name1", "loginUser1", "emailUser1@mail.ru",
                LocalDate.of(1996, 1, 28));
        User user2 = new User(0, "Name2", "loginUser2", "emailUser2@mail.ru",
                LocalDate.of(1997, 9, 7));
        filmDbStorage.save(film1);
        filmDbStorage.save(film2);
        userDbStorage.save(user1);
        userDbStorage.save(user2);
        likeDbStorage.saveLike(1, 1);
        likeDbStorage.saveLike(1, 2);
        likeDbStorage.saveLike(2, 1);

        assertTrue(filmDbStorage.containsInStorage(film1.getId()));
        assertEquals(film1, filmDbStorage.getFilmById(1));
        assertEquals(film2, filmDbStorage.getFilmById(2));

        film1.setDescription("Измененное описание фильма");
        filmDbStorage.update(film1);
        Film updatedFilm = filmDbStorage.getFilmById(1);

        assertEquals(updatedFilm.getDescription(), "Измененное описание фильма");
        assertEquals(2, (int) likeDbStorage.getCountLikesByFilmId(1));

        likeDbStorage.deleteLike(1, 1);
        likeDbStorage.deleteLike(2, 1);

        assertEquals(1, (int) likeDbStorage.getCountLikesByFilmId(1));
        assertEquals(0, (int) likeDbStorage.getCountLikesByFilmId(2));

        List<Film> popularFilms = filmDbStorage.getPopularFilms(2);

        assertEquals(2, popularFilms.size());
        assertEquals(popularFilms.get(0), film1);
        assertEquals(popularFilms.get(1), film2);

        genreDbStorage.setGenre(film1);
        List<Genre> genres = genreDbStorage.loadGenres(film1.getId());

        assertEquals(2, genres.size());
        assertEquals(new Genre(1, "Комедия"), genres.get(0));
        assertEquals(new Genre(6, "Боевик"), genres.get(1));
        assertEquals(6, genreDbStorage.getAllGenres().size());
        assertEquals(new Genre(4, "Триллер"), genreDbStorage.getGenreById(4));

        assertEquals(5, mpaDbStorage.getAllMpa().size());
        assertEquals(mpaDbStorage.getMpaById(4), new Mpa(4, "R"));
    }
}
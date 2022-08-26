package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.MarkDao;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final MarkDao markDbStorage;

    @Test
    public void getFilmRecommendationsTest() {
        User user1 = new User(1L, "Name1", "loginUser1", "emailUser1@mail.ru",
                LocalDate.of(1996, 1, 28));
        userDbStorage.save(user1);
        User user2 = new User(2L, "Name2", "loginUser2", "emailUser2@mail.ru",
                LocalDate.of(1997, 9, 7));
        userDbStorage.save(user2);
        User user3 = new User(3L, "Name3", "loginUser3", "emailUser3@mail.ru",
                LocalDate.of(1997, 9, 7));
        userDbStorage.save(user3);
        Film film1 = new Film(1L, "Название фильма", "Описание фильма",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<Genre>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<Director>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film1);
        Film film2 = new Film(2L, "Название фильма2", "Описание фильма2",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<Genre>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<Director>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film2);
        Film film3 = new Film(3L, "Название фильма3", "Описание фильма3",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<Genre>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<Director>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film3);

        markDbStorage.save(film1.getId(), user1.getId(), (byte) 10);
        markDbStorage.save(film1.getId(), user2.getId(), (byte) 10);
        markDbStorage.save(film1.getId(), user3.getId(), (byte) 3);
        markDbStorage.save(film2.getId(), user2.getId(), (byte) 10);
        markDbStorage.save(film2.getId(), user3.getId(), (byte) 10);
        markDbStorage.save(film3.getId(), user2.getId(), (byte) 10);

        assertThat(filmDbStorage.getFilmById(1L).getRate()).isGreaterThan(0F);
        assertThat(filmDbStorage.getFilmById(2L).getRate()).isGreaterThan(0F);
        assertThat(filmDbStorage.getFilmById(3L).getRate()).isGreaterThan(0F);

        assertThat(filmDbStorage.getFilmRecommendations(user1.getId())).contains(film2).contains(film3);
    }
}
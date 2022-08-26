package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final MarkDao markDbStorage;

    @Order(1)
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
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film1);
        Film film2 = new Film(2L, "Название фильма2", "Описание фильма2",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film2);
        Film film3 = new Film(3L, "Название фильма3", "Описание фильма3",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film3);

        markDbStorage.save(film1.getId(), user1.getId(), (byte) 10);
        markDbStorage.save(film1.getId(), user2.getId(), (byte) 10);
        markDbStorage.save(film1.getId(), user3.getId(), (byte) 3);
        markDbStorage.save(film2.getId(), user2.getId(), (byte) 10);
        markDbStorage.save(film2.getId(), user3.getId(), (byte) 2);
        markDbStorage.save(film3.getId(), user2.getId(), (byte) 10);

        assertThat(filmDbStorage.getFilmById(1L).getRate()).isGreaterThan(0F);
        assertThat(filmDbStorage.getFilmById(2L).getRate()).isGreaterThan(0F);
        assertThat(filmDbStorage.getFilmById(3L).getRate()).isGreaterThan(0F);

        assertThat(filmDbStorage.getFilmRecommendations(user1.getId())).contains(film2).contains(film3);

    }

    @Order(2)
    @Test
    public void getUSerCommonFilmsTest() {
        User user4 = new User(4L, "Name4", "loginUser4", "emailUser4@mail.ru",
                LocalDate.of(1996, 1, 28));
        userDbStorage.save(user4);
        User user5 = new User(5L, "Name5", "loginUser5", "emailUser5@mail.ru",
                LocalDate.of(1997, 9, 7));
        userDbStorage.save(user5);

        Film film4 = new Film(4L, "Название фильма4", "Описание фильма4",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film4);
        Film film5 = new Film(5L, "Название фильма5", "Описание фильма5",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film5);
        Film film6 = new Film(6L, "Название фильма6", "Описание фильма6",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(new Director((long) 1, "Спилберг"))), new Mpa(1, "G"),
                0.0F);
        filmDbStorage.save(film6);

        markDbStorage.save(film4.getId(), user4.getId(), (byte) 10);
        markDbStorage.save(film4.getId(), user5.getId(), (byte) 10);
        markDbStorage.save(film5.getId(), user4.getId(), (byte) 3);
        markDbStorage.save(film5.getId(), user5.getId(), (byte) 9);
        markDbStorage.save(film6.getId(), user4.getId(), (byte) 8);
        markDbStorage.save(film6.getId(), user5.getId(), (byte) 7);

        assertThat(filmDbStorage.getUsersCommonFilms(user4.getId(), user5.getId()))
                .contains(film4).contains(film6).doesNotContain(film5);

    }
}
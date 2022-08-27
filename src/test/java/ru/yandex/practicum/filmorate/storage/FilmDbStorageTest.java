package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final UserService userService;
    private final FilmService filmService;
    private final DirectorService directorService;

    @Order(1)
    @Test
    public void getFilmRecommendationsTest() {
        User user1 = new User(1L, "Name1", "loginUser1", "emailUser1@mail.ru",
                LocalDate.of(1996, 1, 28));
        userService.save(user1);
        User user2 = new User(2L, "Name2", "loginUser2", "emailUser2@mail.ru",
                LocalDate.of(1997, 9, 7));
        userService.save(user2);
        User user3 = new User(3L, "Name3", "loginUser3", "emailUser3@mail.ru",
                LocalDate.of(1997, 9, 7));
        userService.save(user3);
        Film film1 = new Film(1L, "Название фильма", "Описание фильма",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film1);
        Film film2 = new Film(2L, "Название фильма2", "Описание фильма2",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film2);
        Film film3 = new Film(3L, "Название фильма3", "Описание фильма3",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film3);

        filmService.saveMark(film1.getId(), user1.getId(), (byte) 10);
        filmService.saveMark(film1.getId(), user2.getId(), (byte) 10);
        filmService.saveMark(film1.getId(), user3.getId(), (byte) 3);
        filmService.saveMark(film2.getId(), user2.getId(), (byte) 10);
        filmService.saveMark(film2.getId(), user3.getId(), (byte) 2);
        filmService.saveMark(film3.getId(), user2.getId(), (byte) 10);

        assertThat(filmService.getFilmById(1L).getRate()).isGreaterThan(0F);
        assertThat(filmService.getFilmById(2L).getRate()).isGreaterThan(0F);
        assertThat(filmService.getFilmById(3L).getRate()).isGreaterThan(0F);

        assertThat(filmService.getFilmRecommendations(user1.getId()))
                .contains(film2).contains(film3).doesNotContain(film1);

    }

    @Order(2)
    @Test
    public void getUserCommonFilmsTest() {
        User user4 = new User(4L, "Name4", "loginUser4", "emailUser4@mail.ru",
                LocalDate.of(1996, 1, 28));
        userService.save(user4);
        User user5 = new User(5L, "Name5", "loginUser5", "emailUser5@mail.ru",
                LocalDate.of(1997, 9, 7));
        userService.save(user5);

        Film film4 = new Film(4L, "Название фильма4", "Описание фильма4",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film4);
        Film film5 = new Film(5L, "Название фильма5", "Описание фильма5",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film5);
        Film film6 = new Film(6L, "Название фильма6", "Описание фильма6",
                LocalDate.of(1976, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film6);

        filmService.saveMark(film4.getId(), user4.getId(), (byte) 10);
        filmService.saveMark(film4.getId(), user5.getId(), (byte) 10);
        filmService.saveMark(film5.getId(), user4.getId(), (byte) 3);
        filmService.saveMark(film5.getId(), user5.getId(), (byte) 9);
        filmService.saveMark(film6.getId(), user4.getId(), (byte) 8);
        filmService.saveMark(film6.getId(), user5.getId(), (byte) 7);

        assertThat(filmService.getUsersCommonFilms(user4.getId(), user5.getId()))
                .contains(film4).contains(film6).doesNotContain(film5);

    }

    @Order(3)
    @Test
    public void getPopularFilmsByGenreTest() {

        User user6 = new User(6L, "Name6", "loginUser6", "emailUser6@mail.ru",
                LocalDate.of(1996, 1, 28));
        userService.save(user6);
        User user7 = new User(7L, "Name7", "loginUser7", "emailUser7@mail.ru",
                LocalDate.of(1997, 9, 7));
        userService.save(user7);

        Film film7 = new Film(7L, "Название фильма7", "Описание фильма7",
                LocalDate.of(1990, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film7);
        Film film8 = new Film(8L, "Название фильма8", "Описание фильма8",
                LocalDate.of(1985, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film8);
        Film film9 = new Film(9L, "Название фильма9", "Описание фильма9",
                LocalDate.of(1985, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film9);

        filmService.saveMark(film7.getId(), user6.getId(), (byte) 8);
        filmService.saveMark(film8.getId(), user6.getId(), (byte) 9);
        filmService.saveMark(film8.getId(), user7.getId(), (byte) 10);
        filmService.saveMark(film9.getId(), user7.getId(), (byte) 10);

        assertThat(filmService.getPopularFilms(5, 6, null))
                .contains(film7).contains(film9).doesNotContain(film8);
    }

    @Order(4)
    @Test
    public void getPopularFilmsByYearTest() {

        User user8 = new User(8L, "Name8", "loginUser8", "emailUser8@mail.ru",
                LocalDate.of(1996, 1, 28));
        userService.save(user8);
        User user9 = new User(9L, "Name9", "loginUser9", "emailUser9@mail.ru",
                LocalDate.of(1997, 9, 7));
        userService.save(user9);

        Film film10 = new Film(10L, "Название фильма10", "Описание фильма10",
                LocalDate.of(1990, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film10);
        Film film11 = new Film(11L, "Название фильм10", "Описание фильма10",
                LocalDate.of(1985, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film11);
        Film film12 = new Film(12L, "Название фильма11", "Описание фильма11",
                LocalDate.of(1985, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film12);

        filmService.saveMark(film10.getId(), user8.getId(), (byte) 8);
        filmService.saveMark(film10.getId(), user9.getId(), (byte) 9);
        filmService.saveMark(film11.getId(), user8.getId(), (byte) 10);
        filmService.saveMark(film12.getId(), user9.getId(), (byte) 10);

        assertThat(filmService.getPopularFilms(10, null, 1985))
                .contains(film11).contains(film12).doesNotContain(film10);
    }

    @Order(5)
    @Test
    public void getPopularFilmsByGenreAndYearTest() {

        User user10 = new User(10L, "Name10", "loginUser10", "emailUser10@mail.ru",
                LocalDate.of(1996, 1, 28));
        userService.save(user10);
        User user11 = new User(11L, "Name11", "loginUser11", "emailUser11@mail.ru",
                LocalDate.of(1997, 9, 7));
        userService.save(user11);

        Film film13 = new Film(13L, "Название фильма13", "Описание фильма13",
                LocalDate.of(1985, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film13);
        Film film14 = new Film(14L, "Название фильма14", "Описание фильма14",
                LocalDate.of(1985, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film14);
        Film film15 = new Film(15L, "Название фильма15", "Описание фильма15",
                LocalDate.of(1985, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(), new Mpa(1, "G"),
                0.0F);
        filmService.create(film15);

        filmService.saveMark(film13.getId(), user10.getId(), (byte) 8);
        filmService.saveMark(film14.getId(), user10.getId(), (byte) 9);
        filmService.saveMark(film14.getId(), user11.getId(), (byte) 10);
        filmService.saveMark(film15.getId(), user11.getId(), (byte) 10);

        assertThat(filmService.getPopularFilms(10, 1, 1985))
                .contains(film15).doesNotContain(film13);
    }

    @Order(6)
    @Test
    public void getSortedDirectorsFilmsTest() {

        User user12 = new User(12L, "Name12", "loginUser12", "emailUser12@mail.ru",
                LocalDate.of(1996, 1, 28));
        userService.save(user12);
        User user13 = new User(13L, "Name13", "loginUser13", "emailUser13@mail.ru",
                LocalDate.of(1997, 9, 7));
        userService.save(user13);

        Director director1 = new Director(1L, "Peter Jackson");
        directorService.create(director1);
        Director director2 = new Director(2L, "Lana Wachowski");
        directorService.create(director2);
        Director director3 = new Director(3L, "Lilly Wachowski");
        directorService.create(director3);

        assertThat(directorService.getAllDirectors()).containsOnly(director1, director2, director3);

        Film film16 = new Film(16L, "Название фильма16", "Описание фильма16",
                LocalDate.of(2001, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(director1)), new Mpa(1, "G"),
                0.0F);
        filmService.create(film16);
        Film film17 = new Film(17L, "Название фильма17", "Описание фильма17",
                LocalDate.of(2002, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"))),
                new HashSet<>(Set.of(director1)), new Mpa(1, "G"),
                0.0F);
        filmService.create(film17);
        Film film18 = new Film(18L, "Название фильма18", "Описание фильма18",
                LocalDate.of(2003, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(director1)), new Mpa(1, "G"),
                0.0F);
        filmService.create(film18);
        Film film19 = new Film(19L, "Название фильма19", "Описание фильма19",
                LocalDate.of(1999, 11, 21), 119,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(director2, director3)), new Mpa(1, "G"), 0.0F);
        filmService.create(film19);

        filmService.saveMark(film16.getId(), user12.getId(), (byte) 8);
        filmService.saveMark(film16.getId(), user13.getId(), (byte) 8);
        filmService.saveMark(film17.getId(), user12.getId(), (byte) 7);
        filmService.saveMark(film17.getId(), user13.getId(), (byte) 7);
        filmService.saveMark(film18.getId(), user12.getId(), (byte) 9);
        filmService.saveMark(film18.getId(), user13.getId(), (byte) 9);
        filmService.saveMark(film19.getId(), user12.getId(), (byte) 10);
        filmService.saveMark(film19.getId(), user13.getId(), (byte) 10);

        assertThat(filmService.getSortedDirectorsFilms(1L, FilmSortBy.YEAR))
                .contains(film16).contains(film17).contains(film18).doesNotContain(film19);

        assertThat(filmService.getSortedDirectorsFilms(1L, FilmSortBy.MARKS))
                .contains(film18).contains(film16).contains(film17).doesNotContain(film19);

        assertThat(filmService.getSortedDirectorsFilms(1L, FilmSortBy.LIKES))
                .contains(film18).contains(film16).contains(film17).doesNotContain(film19);

        assertThat(filmService.getSortedDirectorsFilms(2L, FilmSortBy.YEAR))
                .contains(film19).doesNotContain(film16).doesNotContain(film17).doesNotContain(film18);

        assertThat(filmService.getSortedDirectorsFilms(2L, FilmSortBy.MARKS))
                .contains(film19).doesNotContain(film16).doesNotContain(film17).doesNotContain(film18);

        assertThat(filmService.getSortedDirectorsFilms(2L, FilmSortBy.LIKES))
                .contains(film19).doesNotContain(film16).doesNotContain(film17).doesNotContain(film18);

        assertThat(filmService.getSortedDirectorsFilms(3L, FilmSortBy.YEAR))
                .contains(film19).doesNotContain(film16).doesNotContain(film17).doesNotContain(film18);

        assertThat(filmService.getSortedDirectorsFilms(3L, FilmSortBy.MARKS))
                .contains(film19).doesNotContain(film16).doesNotContain(film17).doesNotContain(film18);

        assertThat(filmService.getSortedDirectorsFilms(3L, FilmSortBy.LIKES))
                .contains(film19).doesNotContain(film16).doesNotContain(film17).doesNotContain(film18);
    }

    @Order(7)
    @Test
    public void searchFilmsTest() {
        Director director20 = directorService.create(new Director(0L, "Владимир Иванов"));
        Director director21 = directorService.create(new Director(0L, "Галина Боровикова"));
        Director director22 = directorService.create(new Director(0L, "Таушева Иванов"));

        Film film20 = new Film(0L, "Название фильма20777", "Описание фильма20",
                LocalDate.of(1976, 11, 20), 20,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(director20)),
                new Mpa(1, "G"),
                0.0F);
        filmService.create(film20);
        Film film21 = new Film(0L, "Название фильма21", "Описание фильма21",
                LocalDate.of(1976, 11, 21), 21,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(director21)),
                new Mpa(1, "G"),
                0.0F);
        filmService.create(film21);
        Film film22 = new Film(0L, "Малина22", "Описание фильма22",
                LocalDate.of(1976, 11, 22), 22,
                new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(6, "Боевик"))),
                new HashSet<>(Set.of(director20, director22)),
                new Mpa(1, "G"),
                0.0F);
        filmService.create(film22);

        List<Film> resultByTitle = filmService.search("ильма20777", "TiTlE");
        assertThat(resultByTitle.size()).isEqualTo(1);
        assertThat("Название фильма20777").isEqualTo(resultByTitle.get(0).getName());
        assertThat(resultByTitle).contains(film20);

        List<Film> resultByDirector = filmService.search("ИВАно", "DiReCtOr");
        assertThat(resultByDirector.size()).isEqualTo(2);
        assertThat("Название фильма20777").isEqualTo(resultByDirector.get(0).getName());
        assertThat("Малина22").isEqualTo(resultByDirector.get(1).getName());
        assertThat(resultByDirector).contains(film20);
        assertThat(resultByDirector).contains(film22);

        List<Film> resultByTitleAndDirector = filmService.search("Алина", "dIrEcToR,tItLe");
        assertThat(resultByTitleAndDirector.size()).isEqualTo(2);
        assertThat("Название фильма21").isEqualTo(resultByTitleAndDirector.get(0).getName());
        assertThat("Малина22").isEqualTo(resultByTitleAndDirector.get(1).getName());
    }
}
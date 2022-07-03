package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController controller = new UserController();

    @Test
    void createValidUsers() {
        User userGarry = new User(0, "Garry", "ochkarik", "garry.potter@yandex.ru",
                LocalDate.of(1989, 7, 23));
        User userSaul = new User(0, "Saul", "lawyer", "saul.goodman@yandex.ru",
                LocalDate.of(1962, 10, 22));

        controller.create(userGarry);
        controller.create(userSaul);

        assertEquals(2, controller.readAll().size(), "Контроллер не создал пользователей");
        assertEquals(1, (int) userGarry.getId());
        assertEquals(2, (int) userSaul.getId());
    }

    @Test
    void createWrongEmailUser() {
        User userGarry = new User(0, "Garry", "ochkarik", "",
                LocalDate.of(1989, 7, 23));
        User userSaul = new User(0, "Saul", "lawyer", "saul.goodman.yandex.ru",
                LocalDate.of(1962, 10, 22));

        assertThrows(ValidationException.class, () -> controller.create(userGarry));
        assertThrows(ValidationException.class, () -> controller.create(userSaul));
    }

    @Test
    void createWrongLoginUser() {
        User userGarry = new User(0, "Garry", "", "garry.potter@yandex.ru",
                LocalDate.of(1989, 7, 23));
        User userSaul = new User(0, "Saul", "law yer", "saul.goodman@yandex.ru",
                LocalDate.of(1962, 10, 22));

        assertThrows(ValidationException.class, () -> controller.create(userGarry));
        assertThrows(ValidationException.class, () -> controller.create(userSaul));
    }

    @Test
    void createEmptyNameUser() {
        User userSaul = new User(0, "", "lawyer", "saul.goodman@yandex.ru",
                LocalDate.of(1962, 10, 22));

        controller.create(userSaul);

        assertEquals(userSaul.getName(), userSaul.getLogin());
    }

    @Test
    void createWrongBirthdayUser() {
        User userGarry = new User(0, "Garry", "ochkarik", "garry.potter@yandex.ru",
                LocalDate.of(2989, 7, 23));

        assertThrows(ValidationException.class, () -> controller.create(userGarry));
    }

    @Test
    void updateValidUsers() {
        List<User> listUser;
        User userGarry = new User(0, "Garry", "ochkarik", "garry.potter@yandex.ru",
                LocalDate.of(1989, 7, 23));
        User userSaul = new User(0, "Saul", "lawyer", "saul.goodman@yandex.ru",
                LocalDate.of(1962, 10, 22));

        controller.create(userGarry);
        controller.create(userSaul);
        listUser = controller.readAll();

        assertTrue(listUser.contains(userGarry));
        assertTrue(listUser.contains(userSaul));

        User updatedUserGarry = new User(1, "Garry", "ochkarik", "garry.potter@gmail.ru",
                LocalDate.of(1989, 7, 23));
        User updatedUserSaul = new User(2, "Saul", "lawyer", "saul.goodman@gmail.ru",
                LocalDate.of(1962, 10, 22));

        controller.update(updatedUserGarry);
        controller.update(updatedUserSaul);
        listUser = controller.readAll();

        assertTrue(listUser.contains(updatedUserGarry));
        assertTrue(listUser.contains(updatedUserSaul));
        assertFalse(listUser.contains(userGarry));
        assertFalse(listUser.contains(userSaul));
    }

    @Test
    void updateWrongUsers() {
        User userGarry = new User(0, "Garry", "ochkarik", "garry.potter@yandex.ru",
                LocalDate.of(1989, 7, 23));
        User userSaul = new User(0, "Saul", "lawyer", "saul.goodman@yandex.ru",
                LocalDate.of(1962, 10, 22));

        controller.create(userGarry);
        controller.create(userSaul);

        User wrongIdUserGarry = new User(-1, "Garry", "ochkarik", "garry.potter@gmail.ru",
                LocalDate.of(1989, 7, 23));
        User wrongIdUserSaul = new User(478, "Saul", "lawyer", "saul.goodman@gmail.ru",
                LocalDate.of(1962, 10, 22));

        assertThrows(ValidationException.class, () -> controller.update(wrongIdUserGarry));
        assertThrows(ValidationException.class, () -> controller.update(wrongIdUserSaul));
    }

    @Test
    void readAll() {
        User userGarry = new User(0, "Garry", "ochkarik", "garry.potter@yandex.ru",
                LocalDate.of(1989, 7, 23));
        User userSaul = new User(0, "Saul", "lawyer", "saul.goodman@yandex.ru",
                LocalDate.of(1962, 10, 22));

        controller.create(userGarry);
        controller.create(userSaul);

        List<User> list = controller.readAll();

        assertEquals(2, list.size());
        assertTrue(list.contains(userGarry));
        assertTrue(list.contains(userSaul));
    }
}
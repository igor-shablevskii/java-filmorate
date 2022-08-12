package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.impl.FriendDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final FriendDbStorage friendDbStorage;

    @Test
    public void createAndUpdateUser() {
        User user3 = new User(0, "Name3", "loginUser3", "emailUser3@mail.ru",
                LocalDate.of(1997, 10, 28));
        User user4 = new User(0, "Name4", "loginUser4", "emailUser4@mail.ru",
                LocalDate.of(1998, 1, 17));
        User user5 = new User(0, "Name5", "loginUser5", "emailUser5@mail.ru",
                LocalDate.of(2000, 7, 30));
        userDbStorage.save(user3);
        userDbStorage.save(user4);
        userDbStorage.save(user5);

        assertTrue(userDbStorage.containsInStorage(user3.getId()));
        assertTrue(userDbStorage.containsInStorage(user4.getId()));
        assertTrue(userDbStorage.containsInStorage(user5.getId()));

        User savedUser = userDbStorage.getUserById(user3.getId());
        savedUser.setEmail("another.mailUser3@mail.ru");
        userDbStorage.update(savedUser);

        assertEquals("another.mailUser3@mail.ru", savedUser.getEmail());

        friendDbStorage.saveFriend(user3.getId(), user4.getId());
        friendDbStorage.saveFriend(user3.getId(), user5.getId());

        List<Integer> listFriends = friendDbStorage.getFriendsByUserId(user3.getId());

        assertEquals(2, listFriends.size());

        friendDbStorage.deleteFriend(user3.getId(), user4.getId());
        listFriends = friendDbStorage.getFriendsByUserId(user3.getId());

        assertEquals(1, listFriends.size());
    }
}

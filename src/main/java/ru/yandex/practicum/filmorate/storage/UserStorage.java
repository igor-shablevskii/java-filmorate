package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;

public interface UserStorage {

    User save(User user);

    void delete(User user);

    User update(User user);

    boolean containsInStorage(int userId);

    List<User> getAllUsers();

    User getUserById(int userId);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    HashSet<Integer> getFriendsByUserId(int userId);
}
package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(int userId) {
        final User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return user;
    }

    public User create(User user) {
        return userStorage.save(user);
    }

    public User update(User user) {
        if (userStorage.getUserById(user.getId()) == null) {
            throw new NotFoundException("User with id = " + user.getId() + " not found");
        }
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        if (!userStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        if (!userStorage.containsInStorage(friendId)) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        if (!userStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        if (!userStorage.containsInStorage(friendId)) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsByUserId(int userId) {
        if (!userStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return userStorage.getFriendsByUserId(userId)
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        List<User> friends = getFriendsByUserId(userId);
        List<User> otherFriends = getFriendsByUserId(otherUserId);
        return friends.stream().filter(otherFriends::contains).collect(Collectors.toList());
    }
}
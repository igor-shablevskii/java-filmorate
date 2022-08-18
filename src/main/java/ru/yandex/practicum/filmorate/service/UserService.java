package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDbStorage;
    private final FriendDao friendDbStorage;

    @Autowired
    public UserService(UserDao userDbStorage, FriendDao friendDbStorage) {
        this.userDbStorage = userDbStorage;
        this.friendDbStorage = friendDbStorage;
    }

    public User save(User user) {
        return userDbStorage.save(user);
    }

    public User update(User user) {
        if (!userDbStorage.containsInStorage(user.getId())) {
            throw new NotFoundException("User with id = " + user.getId() + " not found");
        }
        return userDbStorage.update(user);
    }

    public User getUserById(int userId) {
        if (!userDbStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return userDbStorage.getUserById(userId);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        if (!userDbStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        if (!userDbStorage.containsInStorage(friendId)) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        }
        friendDbStorage.saveFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        if (!userDbStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        if (!userDbStorage.containsInStorage(friendId)) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        }
        friendDbStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsByUserId(int userId) {
        if (!userDbStorage.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return friendDbStorage.getFriendsByUserId(userId)
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        List<User> friends = getFriendsByUserId(userId);
        List<User> otherFriends = getFriendsByUserId(otherUserId);
        return friends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toList());
    }

    public void deleteUserById(int userId) {
        userDbStorage.deleteUserById(userId);
    }
}
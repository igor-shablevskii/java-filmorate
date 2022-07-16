package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    UserStorage userStorage;

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
        Set<Integer> friendsId = userStorage.getFriendsByUserId(userId);
        List<User> listFriends = new ArrayList<>();
        for (Integer id : friendsId) {
            listFriends.add(userStorage.getUserById(id));
        }
        return listFriends;
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        List<User> listCommonFriends = new ArrayList<>();
        List<User> friends = getFriendsByUserId(userId);
        List<User> otherFriends = getFriendsByUserId(otherUserId);
        for (User user : friends) {
            if (otherFriends.contains(user)) {
                listCommonFriends.add(user);
            }
        }
        return listCommonFriends;
    }
}
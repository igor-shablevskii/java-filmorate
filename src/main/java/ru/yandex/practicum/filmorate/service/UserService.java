package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;
    private final FriendDao friendDao;
    private final FilmService filmDao;
    private final FeedDao feedDao;

    @Autowired
    public UserService(UserDao userDao,
                       FriendDao friendDao,
                       FilmService filmDao,
                       FeedDao feedDao) {
        this.userDao = userDao;
        this.friendDao = friendDao;
        this.filmDao = filmDao;
        this.feedDao = feedDao;
    }

    public User save(User user) {
        return userDao.save(user);
    }

    public User update(User user) {
        if (!userDao.containsInStorage(user.getId())) {
            throw new NotFoundException("User with id = " + user.getId() + " not found");
        }
        return userDao.update(user);
    }

    public User getUserById(int userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return userDao.getUserById(userId);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        if (!userDao.containsInStorage(friendId)) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        }
        feedDao.create(new Feed(userId, EventType.FRIEND, Operation.ADD, friendId));
        friendDao.saveFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        if (!userDao.containsInStorage(friendId)) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        }
        feedDao.create(new Feed(userId, EventType.FRIEND, Operation.REMOVE, friendId));
        friendDao.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsByUserId(int userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return friendDao.getFriendsByUserId(userId)
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

    public List<Feed> getAllFeedsByUserId(int id) {
        return feedDao.getAllFeedsByUserId(id);
    }

    public List<Film> getFilmRecommendations(Integer userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return filmDao.getFilmRecommendations(userId);
    }

    public void deleteUserById(int userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        userDao.deleteUserById(userId);
    }
}
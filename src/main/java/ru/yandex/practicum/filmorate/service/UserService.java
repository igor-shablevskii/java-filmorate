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
        isUserExists(user.getId());
        return userDao.update(user);
    }

    public User getUserById(Long userId) {
        isUserExists(userId);
        return userDao.getUserById(userId);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        isUserExists(userId);
        isUserExists(friendId);
        feedDao.create(new Feed(userId, EventType.FRIEND, Operation.ADD, friendId));
        friendDao.saveFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        isUserExists(userId);
        isUserExists(friendId);
        feedDao.create(new Feed(userId, EventType.FRIEND, Operation.REMOVE, friendId));
        friendDao.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsByUserId(Long userId) {
        isUserExists(userId);
        return friendDao.getFriendsByUserId(userId)
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        List<User> friends = getFriendsByUserId(userId);
        List<User> otherFriends = getFriendsByUserId(otherUserId);
        return friends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toList());
    }

    public List<Feed> getAllFeedsByUserId(Long id) {
        return feedDao.getAllFeedsByUserId(id);
    }

    public List<Film> getFilmRecommendations(Long userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return filmDao.getFilmRecommendations(userId);
    }

    public void deleteUserById(Long userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        userDao.deleteUserById(userId);
    }

    private void isUserExists(Long userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }
}
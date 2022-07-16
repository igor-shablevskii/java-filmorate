package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, HashSet<Integer>> friends = new HashMap<>();
    private Integer id = 0;

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public User save(User user) {
        user.setId(++id);
        friends.put(id, new HashSet<>());
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        friends.get(userId).add(friendId);
        friends.get(friendId).add(userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        friends.get(userId).remove(friendId);
        friends.get(friendId).remove(userId);
    }

    @Override
    public HashSet<Integer> getFriendsByUserId(int userId) {
        return friends.get(userId);
    }

    @Override
    public boolean containsInStorage(int userId) {
        return users.containsKey(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
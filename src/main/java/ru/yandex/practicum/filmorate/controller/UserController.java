package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Update;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        log.info("Get user by id = {}", user.getId());
        return user;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        validate(user);
        User savedUser = userService.save(user);
        log.info("User {} created and added in storage", savedUser);
        return savedUser;
    }

    @PutMapping
    public User update(@RequestBody @Validated(Update.class) User user) {
        User updatedUser = userService.update(user);
        log.info("User {} updated and saved in storage", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public List<User> getAll() {
        List<User> userList = userService.getAllUsers();
        log.info("Get all user, count = {}", userList.size());
        return userList;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Delete friend id = {} user by id = {}", friendId, userId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriendByUserId(@PathVariable Long userId) {
        log.info("Get users friends by id = {}", userId);
        return userService.getFriendsByUserId(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherUserId) {
        List<User> listCommonFriends = userService.getCommonFriends(userId, otherUserId);
        log.info("Get list common friends user id = {} and user id = {}, list ids = {}",
                userId, otherUserId, listCommonFriends.stream().map(User::getId).collect(Collectors.toList()));
        return listCommonFriends;
    }

    @GetMapping(value = "/{id}/feed")
    public List<Feed> getAllFeedsByUserId(@PathVariable Long id) {
        List<Feed> feedList = userService.getAllFeedsByUserId(id);
        log.info("Get list feeds user id = {}, list ids = {}", id, feedList.size());
        return feedList;
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("Delete user by id = {}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping("{id}/recommendations")
    public List<Film> getFilmRecommendations(@PathVariable Long id) {
        List<Film> recommendations = userService.getFilmRecommendations(id);
        log.info("Get recommendations ids = {}", recommendations.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        return recommendations;
    }

    private void validate(User user) {
        String message;
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            message = String.format("Электронная почта пользователя %s пустая или не содержит символ \"@\"", user);
            log.info(message);
            throw new ValidationException(message);
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            message = String.format("Логин пользователя %s пустой или содержит пробелы", user);
            log.info(message);
            throw new ValidationException(message);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            message = String.format("Некорректная дата рождения у пользователя %s", user);
            log.info(message);
            throw new ValidationException(message);
        }
        if (user.getName().isEmpty()) {
            log.info("Пользователь {} с пустым именем для отображения", user);
            user.setName(user.getLogin());
            log.info("Пользователю {} в качестве имени для отображения присвоен его логин {}", user, user.getLogin());
        }
    }
}
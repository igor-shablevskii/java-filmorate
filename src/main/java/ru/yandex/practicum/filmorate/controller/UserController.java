package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Пришел запрос на добавление пользователя {}", user);
        validate(user);
        user.setId(generateId());
        users.put(id, user);
        log.info("Пользователь {} добавлен", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Пришел запрос на обновление пользователя {}", user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь {} обновлен", user);
            return users.get(user.getId());
        } else {
            RuntimeException e = new ValidationException(
                    String.format("Пользователь с идентификатором %d не найден", user.getId()));
            log.info(e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<User> readAll() {
        return new ArrayList<>(users.values());
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

    private Integer generateId() {
        return ++id;
    }
}
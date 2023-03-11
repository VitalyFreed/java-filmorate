package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {
    private static int userId = 1;
    private HashMap<Integer, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private void validateUser(User user) {
        for (User userItem : users.values()) {
            if (userItem.getEmail().equals(user.getEmail())) {
                log.warn("Валидация не пройдена");
                throw new ValidateException("Пользователь уже существует");
            }
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Электронная почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Электронная почта должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Логин не может быть пустым и содержать пробелы");
        }


        String[] splitedBirthday = user.getBirthday().split("-");

        if (new Date(Integer.parseInt(splitedBirthday[0]), Integer.parseInt(splitedBirthday[1]), Integer.parseInt(splitedBirthday[2])).after(new Date())) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Дата рождения не может быть в будущем");
        }
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateUser(user);

        user.setId(userId);
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен");

        userId++;

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);

        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлен");

        return user;
    }
}

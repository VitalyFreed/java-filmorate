package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {
    private int userId = 1;
    private HashMap<Integer, User> users = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    private void validateUser(User user) throws ValidateException {
        for (User userItem : users.values()) {
            if (userItem.getEmail().equals(user.getEmail())) {
                log.warn("Валидация не пройдена");
                throw new ValidateException("Пользователь уже существует");
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        String[] splitedBirthday = user.getBirthday().split("-");
        LocalDateTime localDateTime = LocalDateTime.of(
                        Integer.parseInt(splitedBirthday[0]),
                        Integer.parseInt(splitedBirthday[1]),
                        Integer.parseInt(splitedBirthday[2]),
                        0,
                        0
                );

        if (localDateTime.isAfter(LocalDateTime.now())) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Дата рождения не может быть в будущем");
        }
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        return ResponseEntity.status(200).body(users.values());
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        try {
            validateUser(user);

            user.setId(userId);
            users.put(user.getId(), user);
            log.info("Пользователь успешно добавлен");

            userId++;
        } catch (ValidateException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(500).body(user);
        }
        for (User userItem : users.values()) {
            if (userItem.getEmail().equals(user.getEmail())) {
                return ResponseEntity.status(200).body(userItem);
            }
        }

        return ResponseEntity.status(200).body(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);

        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлен");

        return user;
    }
}

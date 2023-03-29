package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static int filmId = 1;
    private HashMap<Integer, Film> films = new HashMap<>();
    private final int DESCRIPTION_MAX_LENGTH = 200;
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Название не может быть пустым");
        }

        if (film.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Максимальная длина описания — " + DESCRIPTION_MAX_LENGTH + " символов");
        }

        if (film.getDuration() <= 0) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Продолжительность фильма должна быть положительной");
        }

        String[] splitedReleaseDate = film.getReleaseDate().split("-");
        LocalDateTime localDateTime = LocalDateTime.of
                (
                        Integer.parseInt(splitedReleaseDate[0]),
                        Integer.parseInt(splitedReleaseDate[1]),
                        Integer.parseInt(splitedReleaseDate[2]),
                        0,
                        0
                );

        if (localDateTime.isBefore(LocalDateTime.of(1895, 12, 28, 0, 0))) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        try {
            validateFilm(film);

            film.setId(filmId++);
            films.put(film.getId(), film);
            log.info("Фильм успешно добавлен");
        } catch (ValidateException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(500).body(film);
        }

        return ResponseEntity.status(200).body(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            return ResponseEntity.status(404).body(film);
        }

        validateFilm(film);

        films.put(film.getId(), film);
        log.info("Фильм успешно обновлен");

        return ResponseEntity.status(200).body(film);
    }
}

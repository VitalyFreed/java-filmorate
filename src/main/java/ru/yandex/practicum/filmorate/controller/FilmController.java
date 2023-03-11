package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static int filmId = 0;
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

        if (new Date(Integer.parseInt(splitedReleaseDate[0]), Integer.parseInt(splitedReleaseDate[1]), Integer.parseInt(splitedReleaseDate[2])).before(new Date(28, 12, 1895))) {
            log.warn("Валидация не пройдена");
            throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);

        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен");

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);

        films.put(film.getId(), film);
        log.info("Фильм успешно обновлен");

        return film;
    }
}

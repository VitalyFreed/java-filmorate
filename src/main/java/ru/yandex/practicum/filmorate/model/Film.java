package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Film {
    private int id;
    @NotNull
    @NotEmpty
    private String name;
    private String description;
    private String releaseDate;
    private double duration;
}

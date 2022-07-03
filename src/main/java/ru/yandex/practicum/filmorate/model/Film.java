package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Film {
    private Integer id;
    private String name;
    private String description;
    private final LocalDate releaseDate;
    private final Integer duration;
}

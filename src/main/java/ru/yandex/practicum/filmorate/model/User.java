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
public class User {
    private Integer id;
    private String name;
    private String login;
    private String email;
    private LocalDate birthday;
}

package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Builder
public class User {
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    @NotBlank
    private String login;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    private LocalDate birthday;
}
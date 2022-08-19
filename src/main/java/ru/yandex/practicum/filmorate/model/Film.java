package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Builder
public class Film {
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private Integer duration;
    @JsonIgnore
    private Integer rate = 0;
    private Mpa mpa;
    private Set<Genre> genres = new LinkedHashSet<>();
    private Set<Director> directors = new HashSet<>();
}
package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "reviewId")
@Builder
public class Review {
    private Integer reviewId;
    @NotNull
    private String content;
    private Integer useful;
    @JsonProperty(required = true)
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    private Set<Reaction> userReactions = new HashSet<>();
}

package ru.yandex.practicum.filmorate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность фильма для внутреннего использования.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Integer id;
    private String name;
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    private Integer duration;

    @Builder.Default
    private Set<Integer> likes = new HashSet<>();
}
package ru.yandex.practicum.filmorate.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.model.entity.Genre;
import ru.yandex.practicum.filmorate.model.entity.Mpa;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO для передачи данных о фильме через API.
 */
public record FilmDto(
        Integer id,

        @NotBlank(message = "Название фильма не может быть пустым")
        String name,

        @Size(max = 200, message = "Описание не может превышать 200 символов")
        String description,

        @NotNull(message = "Дата релиза обязательна")
        @ReleaseDate
        LocalDate releaseDate,

        @NotNull(message = "Продолжительность обязательна")
        @Positive(message = "Продолжительность должна быть положительным числом")
        Integer duration,

        Mpa mpa,

        Set<Genre> genres
) {

    public FilmDto(Integer id, String name, String description,
                   LocalDate releaseDate, Integer duration) {
        this(id, name, description, releaseDate, duration, null, Set.of());
    }
}
package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateValidFilm() {
        FilmDto film = new FilmDto(
                null,
                "Test Film",
                "Test description",
                LocalDate.of(2020, 1, 1),
                120
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        FilmDto film = new FilmDto(
                null,
                "",
                "Test description",
                LocalDate.of(2020, 1, 1),
                120
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        FilmDto film = new FilmDto(
                null,
                "Test Film",
                "a".repeat(201),
                LocalDate.of(2020, 1, 1),
                120
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Описание не может превышать 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        FilmDto film = new FilmDto(
                null,
                "Test Film",
                "Test description",
                LocalDate.of(2030, 1, 1),
                -10
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Продолжительность должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        FilmDto film = new FilmDto(
                null,
                "Test Film",
                "Test description",
                LocalDate.of(1895, 12, 27),
                120
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", violations.iterator().next().getMessage());
    }
}
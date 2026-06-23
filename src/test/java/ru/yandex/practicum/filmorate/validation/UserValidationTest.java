package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.dto.UserDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateValidUser() {
        UserDto user = new UserDto(
                null,
                "test@example.com",
                "testuser",
                "Test User",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        UserDto user = new UserDto(
                null,
                "",
                "testuser",
                "Test User",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenEmailDoesNotContainAtSymbol() {
        UserDto user = new UserDto(
                null,
                "testexample.com",
                "testuser",
                "Test User",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Некорректный формат электронной почты", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenLoginIsEmpty() {
        UserDto user = new UserDto(
                null,
                "test@example.com",
                "",
                "Test User",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        UserDto user = new UserDto(
                null,
                "test@example.com",
                "test user",
                "Test User",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Логин не должен содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() {
        UserDto user = new UserDto(
                null,
                "test@example.com",
                "testuser",
                "Test User",
                LocalDate.of(2030, 1, 1)
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }
}
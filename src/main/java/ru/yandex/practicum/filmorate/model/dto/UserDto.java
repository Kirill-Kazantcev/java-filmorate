package ru.yandex.practicum.filmorate.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * DTO для передачи данных о пользователе через API.
 */
public record UserDto(
        Integer id,

        @NotBlank(message = "Электронная почта не может быть пустой")
        @Email(message = "Некорректный формат электронной почты")
        String email,

        @NotBlank(message = "Логин не может быть пустым")
        @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
        String login,

        String name,

        @NotNull(message = "Дата рождения обязательна")
        @PastOrPresent(message = "Дата рождения не может быть в будущем")
        LocalDate birthday
) {}
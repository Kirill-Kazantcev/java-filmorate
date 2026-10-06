package ru.yandex.practicum.filmorate.model.dto;

/**
 * DTO для рейтинга MPA. Используется в FilmDto и в API-ответах /mpa.
 */
public record MpaDto(Integer id, String name) {
}
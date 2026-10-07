package ru.yandex.practicum.filmorate.model.dto;

/**
 * DTO для жанра. Используется в FilmDto и в API-ответах /genres.
 */
public record GenreDto(Integer id, String name) {
}
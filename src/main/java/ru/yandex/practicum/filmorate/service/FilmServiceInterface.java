package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.dto.FilmDto;

import java.util.List;

/**
 * Интерфейс сервиса для работы с фильмами.
 */
public interface FilmServiceInterface {

    FilmDto create(FilmDto filmDto);

    FilmDto update(FilmDto filmDto);

    FilmDto findById(Integer id);

    List<FilmDto> findAll();

    void delete(Integer id);
}
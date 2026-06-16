package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.entity.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    Optional<Film> findById(Integer id);

    List<Film> findAll();

    void deleteById(Integer id);
}
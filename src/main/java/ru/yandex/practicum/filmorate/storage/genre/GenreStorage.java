package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.util.List;
import java.util.Optional;

/**
 * Хранилище справочника жанров.
 */
public interface GenreStorage {

    List<Genre> findAll();

    Optional<Genre> findById(Integer id);
}
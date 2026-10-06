package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.entity.Mpa;

import java.util.List;
import java.util.Optional;

/**
 * Хранилище справочника рейтингов MPA.
 */
public interface MpaStorage {

    List<Mpa> findAll();

    Optional<Mpa> findById(Integer id);
}
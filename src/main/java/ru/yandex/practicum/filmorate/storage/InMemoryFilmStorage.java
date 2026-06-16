package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.entity.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@SuppressWarnings("unused")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Film save(Film film) {
        film.setId(idGenerator.getAndIncrement());
        films.put(film.getId(), film);
        log.debug("Сохранен фильм: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.debug("Обновлен фильм: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteById(Integer id) {
        films.remove(id);
        log.debug("Удален фильм: id={}", id);
    }
}
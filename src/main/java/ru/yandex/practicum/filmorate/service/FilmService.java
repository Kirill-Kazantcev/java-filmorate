package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FilmService implements FilmServiceInterface {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper filmMapper;

    @Override
    public FilmDto create(FilmDto filmDto) {
        log.debug("Создание фильма: {}", filmDto.name());
        Film film = filmMapper.toEntity(filmDto);
        Film saved = filmStorage.save(film);
        return filmMapper.toDto(saved);
    }

    @Override
    public FilmDto update(FilmDto filmDto) {
        log.debug("Обновление фильма: id={}", filmDto.id());
        filmStorage.findById(filmDto.id())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmDto.id() + " не найден"));
        Film film = filmMapper.toEntity(filmDto);
        Film updated = filmStorage.update(film);
        return filmMapper.toDto(updated);
    }

    @Override
    public FilmDto findById(Integer id) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        return filmMapper.toDto(film);
    }

    @Override
    public List<FilmDto> findAll() {
        log.debug("Запрос всех фильмов");
        return filmStorage.findAll().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        log.debug("Удаление фильма: id={}", id);
        filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        filmStorage.deleteById(id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        log.debug("Добавление лайка: filmId={}, userId={}", filmId, userId);

        Film film = getFilmById(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        boolean added = film.getLikes().add(userId);
        if (added) {
            filmStorage.update(film);
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        } else {
            log.debug("Пользователь {} уже поставил лайк фильму {}", userId, filmId);
        }
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        log.debug("Удаление лайка: filmId={}, userId={}", filmId, userId);

        Film film = getFilmById(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        boolean removed = film.getLikes().remove(userId);
        if (removed) {
            filmStorage.update(film);
            log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
        } else {
            log.warn("Пользователь {} не ставил лайк фильму {}", userId, filmId);
            throw new NotFoundException("Пользователь не ставил лайк этому фильму");
        }
    }

    @Override
    public List<FilmDto> getPopularFilms(Integer count) {
        int limit = (count != null && count > 0) ? count : 10;
        log.debug("Запрос популярных фильмов: count={}", limit);

        List<Film> allFilms = filmStorage.findAll();

        if (allFilms.isEmpty()) {
            log.debug("Нет фильмов для отображения");
            return List.of();
        }

        return allFilms.stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(limit)
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    private Film getFilmById(Integer id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FilmService implements FilmServiceInterface {

    private final FilmStorage filmStorage;
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
                .toList();
    }

    @Override
    public void delete(Integer id) {
        log.debug("Удаление фильма: id={}", id);
        filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        filmStorage.deleteById(id);
    }
}
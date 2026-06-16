package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmServiceInterface;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FilmController {

    private final FilmServiceInterface filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createFilm(@Valid @RequestBody FilmDto filmDto) {
        log.info("Запрос на создание фильма: {}", filmDto.name());  // ← name(), а не getName()
        return filmService.create(filmDto);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto filmDto) {
        log.info("Запрос на обновление фильма: id={}", filmDto.id());  // ← id(), а не getId()
        return filmService.update(filmDto);
    }

    @GetMapping
    public List<FilmDto> getAllFilms() {
        log.info("Запрос на получение списка всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Integer id) {
        log.info("Запрос на получение фильма: id={}", id);
        return filmService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Integer id) {
        log.info("Запрос на удаление фильма: id={}", id);
        filmService.delete(id);
    }
}
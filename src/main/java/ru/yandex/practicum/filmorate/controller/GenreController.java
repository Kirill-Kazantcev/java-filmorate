package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GenreController {

    private final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    @GetMapping
    public List<GenreDto> findAll() {
        log.info("Запрос на получение всех жанров");
        return genreStorage.findAll().stream()
                .map(genreMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public GenreDto findById(@PathVariable Integer id) {
        log.info("Запрос на получение жанра: id={}", id);
        return genreStorage.findById(id)
                .map(genreMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не найден"));
    }
}
package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.entity.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GenreController {

    private final GenreStorage genreStorage;

    @GetMapping
    public List<Genre> findAll() {
        log.info("Запрос на получение всех жанров");
        return genreStorage.findAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable Integer id) {
        log.info("Запрос на получение жанра: id={}", id);
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не найден"));
    }
}
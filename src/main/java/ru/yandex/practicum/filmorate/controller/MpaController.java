package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.entity.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MpaController {

    private final MpaStorage mpaStorage;

    @GetMapping
    public List<Mpa> findAll() {
        log.info("Запрос на получение всех рейтингов MPA");
        return mpaStorage.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable Integer id) {
        log.info("Запрос на получение рейтинга MPA: id={}", id);
        return mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id=" + id + " не найден"));
    }
}
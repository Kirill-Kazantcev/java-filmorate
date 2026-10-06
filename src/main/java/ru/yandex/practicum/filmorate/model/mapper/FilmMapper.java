package ru.yandex.practicum.filmorate.model.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.entity.Film;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Маппер для преобразования Film <-> FilmDto.
 */
@Component
public class FilmMapper {

    public FilmDto toDto(Film film) {
        if (film == null) {
            return null;
        }
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getGenres() == null ? Set.of() : new LinkedHashSet<>(film.getGenres())
        );
    }

    public Film toEntity(FilmDto filmDto) {
        if (filmDto == null) {
            return null;
        }
        Film film = Film.builder()
                .id(filmDto.id())
                .name(filmDto.name())
                .description(filmDto.description())
                .releaseDate(filmDto.releaseDate())
                .duration(filmDto.duration())
                .mpa(filmDto.mpa())
                .build();

        if (filmDto.genres() != null) {
            film.setGenres(new LinkedHashSet<>(filmDto.genres()));
        }
        return film;
    }
}
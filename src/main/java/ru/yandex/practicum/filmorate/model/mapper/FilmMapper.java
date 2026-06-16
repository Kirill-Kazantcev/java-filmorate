package ru.yandex.practicum.filmorate.model.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.entity.Film;

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
                film.getDuration()
        );
    }

    public Film toEntity(FilmDto filmDto) {
        if (filmDto == null) {
            return null;
        }
        return Film.builder()
                .id(filmDto.id())
                .name(filmDto.name())
                .description(filmDto.description())
                .releaseDate(filmDto.releaseDate())
                .duration(filmDto.duration())
                .build();
    }
}
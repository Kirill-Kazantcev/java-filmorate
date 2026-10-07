package ru.yandex.practicum.filmorate.model.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.entity.Genre;

/**
 * Маппер для Genre <-> GenreDto.
 */
@Component
public class GenreMapper {

    public GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreDto(genre.id(), genre.name());
    }

    public Genre toEntity(GenreDto dto) {
        if (dto == null) {
            return null;
        }
        return new Genre(dto.id(), dto.name());
    }
}
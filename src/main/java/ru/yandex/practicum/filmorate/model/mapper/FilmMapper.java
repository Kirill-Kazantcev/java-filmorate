package ru.yandex.practicum.filmorate.model.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.entity.Film;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Маппер для Film <-> FilmDto.
 * Использует MpaMapper и GenreMapper для преобразования вложенных сущностей.
 */
@Component
@RequiredArgsConstructor
public class FilmMapper {

    private final MpaMapper mpaMapper;
    private final GenreMapper genreMapper;

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
                mpaMapper.toDto(film.getMpa()),
                sortedGenreDtos(film.getGenres())
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
                .mpa(mpaMapper.toEntity(filmDto.mpa()))
                .build();

        if (filmDto.genres() != null) {
            film.setGenres(filmDto.genres().stream()
                    .map(genreMapper::toEntity)
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        return film;
    }

    private Set<GenreDto> sortedGenreDtos(Set<ru.yandex.practicum.filmorate.model.entity.Genre> source) {
        if (source == null) {
            return new LinkedHashSet<>();
        }
        return source.stream()
                .map(genreMapper::toDto)
                .sorted(Comparator.comparing(GenreDto::id,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
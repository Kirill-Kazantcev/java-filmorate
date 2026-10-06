package ru.yandex.practicum.filmorate.model.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
                sortedGenres(film.getGenres())
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
            film.setGenres(sortedGenres(filmDto.genres()));
        }
        return film;
    }

    private Set<Genre> sortedGenres(Set<Genre> source) {
        if (source == null) {
            return new LinkedHashSet<>();
        }
        return source.stream()
                .sorted(Comparator.comparing(Genre::id,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
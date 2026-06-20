package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.model.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmService filmService;

    private Film film1;
    private Film film2;
    private User user;

    @BeforeEach
    void setUp() {
        film1 = Film.builder()
                .id(1)
                .name("Film 1")
                .description("Description 1")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .likes(new HashSet<>())
                .build();

        film2 = Film.builder()
                .id(2)
                .name("Film 2")
                .description("Description 2")
                .releaseDate(LocalDate.of(2021, 2, 2))
                .duration(130)
                .likes(new HashSet<>())
                .build();

        user = User.builder()
                .id(1)
                .email("user@test.com")
                .login("user")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    void addLike_ShouldAddLikeToFilm() {
        when(filmStorage.findById(1)).thenReturn(Optional.of(film1));
        when(userStorage.findById(1)).thenReturn(Optional.of(user));
        when(filmStorage.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        filmService.addLike(1, 1);

        assertTrue(film1.getLikes().contains(1));
        verify(filmStorage, times(1)).update(film1);
    }

    @Test
    void addLike_ShouldThrowNotFoundException_WhenFilmNotFound() {
        when(filmStorage.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1, 1));
        verify(filmStorage, never()).update(any(Film.class));
    }

    @Test
    void addLike_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(filmStorage.findById(1)).thenReturn(Optional.of(film1));
        when(userStorage.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1, 1));
        verify(filmStorage, never()).update(any(Film.class));
    }

    @Test
    void removeLike_ShouldRemoveLikeFromFilm() {
        film1.getLikes().add(1);

        when(filmStorage.findById(1)).thenReturn(Optional.of(film1));
        when(userStorage.findById(1)).thenReturn(Optional.of(user));
        when(filmStorage.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        filmService.removeLike(1, 1);

        assertFalse(film1.getLikes().contains(1));
        verify(filmStorage, times(1)).update(film1);
    }

    @Test
    void removeLike_ShouldThrowNotFoundException_WhenFilmNotFound() {
        when(filmStorage.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.removeLike(1, 1));
        verify(filmStorage, never()).update(any(Film.class));
    }

    @Test
    void getPopularFilms_ShouldReturnFilmsSortedByLikes() {
        film1.getLikes().add(1);
        film1.getLikes().add(2);
        film2.getLikes().add(1);

        FilmDto filmDto1 = new FilmDto(1, "Film 1", "Description 1", LocalDate.of(2020, 1, 1), 120);
        FilmDto filmDto2 = new FilmDto(2, "Film 2", "Description 2", LocalDate.of(2021, 2, 2), 130);

        when(filmStorage.findPopular(2)).thenReturn(List.of(film1, film2));
        when(filmMapper.toDto(film1)).thenReturn(filmDto1);
        when(filmMapper.toDto(film2)).thenReturn(filmDto2);

        List<FilmDto> popular = filmService.getPopularFilms(2);

        assertEquals(2, popular.size());
        assertEquals(1, popular.getFirst().id());
        assertEquals(2, popular.get(1).id());
    }

    @Test
    void getPopularFilms_ShouldReturnDefault10_WhenCountIsNull() {
        FilmDto filmDto1 = new FilmDto(1, "Film 1", "Description 1", LocalDate.of(2020, 1, 1), 120);
        FilmDto filmDto2 = new FilmDto(2, "Film 2", "Description 2", LocalDate.of(2021, 2, 2), 130);

        when(filmStorage.findPopular(10)).thenReturn(List.of(film1, film2));
        when(filmMapper.toDto(film1)).thenReturn(filmDto1);
        when(filmMapper.toDto(film2)).thenReturn(filmDto2);

        List<FilmDto> popular = filmService.getPopularFilms(null);

        assertEquals(2, popular.size());
    }

    @Test
    void getPopularFilms_ShouldReturnEmptyList_WhenNoFilms() {
        when(filmStorage.findPopular(5)).thenReturn(List.of());

        List<FilmDto> popular = filmService.getPopularFilms(5);

        assertTrue(popular.isEmpty());
    }
}
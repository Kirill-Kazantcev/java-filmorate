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
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private FilmMapper filmMapper;

    @Mock
    private MpaStorage mpaStorage;

    @Mock
    private GenreStorage genreStorage;

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
    void addLike_ShouldDelegateToStorage() {
        when(filmStorage.findById(1)).thenReturn(Optional.of(film1));
        when(userStorage.findById(1)).thenReturn(Optional.of(user));

        filmService.addLike(1, 1);

        verify(filmStorage, times(1)).addLike(1, 1);
        verify(filmStorage, never()).update(any(Film.class));
    }

    @Test
    void addLike_ShouldThrowNotFoundException_WhenFilmNotFound() {
        when(filmStorage.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1, 1));
        verify(filmStorage, never()).addLike(anyInt(), anyInt());
    }

    @Test
    void addLike_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(filmStorage.findById(1)).thenReturn(Optional.of(film1));
        when(userStorage.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1, 1));
        verify(filmStorage, never()).addLike(anyInt(), anyInt());
    }

    @Test
    void removeLike_ShouldDelegateToStorage() {
        when(filmStorage.findById(1)).thenReturn(Optional.of(film1));
        when(userStorage.findById(1)).thenReturn(Optional.of(user));

        filmService.removeLike(1, 1);

        verify(filmStorage, times(1)).removeLike(1, 1);
        verify(filmStorage, never()).update(any(Film.class));
    }

    @Test
    void removeLike_ShouldThrowNotFoundException_WhenFilmNotFound() {
        when(filmStorage.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.removeLike(1, 1));
        verify(filmStorage, never()).removeLike(anyInt(), anyInt());
    }

    @Test
    void getPopularFilms_ShouldReturnFilmsSortedByLikes() {
        FilmDto filmDto1 = new FilmDto(1, "Film 1", "Description 1",
                LocalDate.of(2020, 1, 1), 120);
        FilmDto filmDto2 = new FilmDto(2, "Film 2", "Description 2",
                LocalDate.of(2021, 2, 2), 130);

        when(filmStorage.findPopular(2)).thenReturn(List.of(film1, film2));
        when(filmMapper.toDto(film1)).thenReturn(filmDto1);
        when(filmMapper.toDto(film2)).thenReturn(filmDto2);

        List<FilmDto> popular = filmService.getPopularFilms(2);

        assertEquals(2, popular.size());
        assertEquals(1, popular.get(0).id());
        assertEquals(2, popular.get(1).id());
    }

    @Test
    void getPopularFilms_ShouldUseDefault10_WhenCountIsNull() {
        when(filmStorage.findPopular(10)).thenReturn(List.of());

        List<FilmDto> popular = filmService.getPopularFilms(null);

        assertTrue(popular.isEmpty());
        verify(filmStorage).findPopular(10);
    }

    @Test
    void getPopularFilms_ShouldUseDefault10_WhenCountIsNegative() {
        when(filmStorage.findPopular(10)).thenReturn(List.of());

        List<FilmDto> popular = filmService.getPopularFilms(-5);

        assertTrue(popular.isEmpty());
        verify(filmStorage).findPopular(10);
    }
}
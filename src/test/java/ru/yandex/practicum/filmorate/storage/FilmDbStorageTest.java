package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.entity.Genre;
import ru.yandex.practicum.filmorate.model.entity.Mpa;
import ru.yandex.practicum.filmorate.model.entity.User;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;


    @Test
    void save_shouldAssignIdAndPersist() {
        Film film = Film.builder()
                .name("Inception")
                .description("Dreams within dreams")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(new Mpa(3, null))
                .build();

        Film saved = filmStorage.save(film);

        assertThat(saved.getId()).isNotNull();
        assertThat(filmStorage.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Inception");
                    assertThat(f.getDuration()).isEqualTo(148);
                    assertThat(f.getMpa()).isNotNull();
                    assertThat(f.getMpa().id()).isEqualTo(3);
                    assertThat(f.getMpa().name()).isEqualTo("PG-13");
                });
    }

    @Test
    void save_withoutMpa_shouldPersist() {
        Film saved = filmStorage.save(Film.builder()
                .name("No MPA").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .build());

        assertThat(filmStorage.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f.getMpa()).isNull());
    }

    @Test
    void save_withGenres_shouldPersistGenres() {
        Set<Genre> genres = new LinkedHashSet<>(List.of(
                new Genre(1, null), new Genre(3, null)));

        Film saved = filmStorage.save(Film.builder()
                .name("Funny Cartoon").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(90)
                .mpa(new Mpa(1, null))
                .genres(genres)
                .build());

        assertThat(filmStorage.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getGenres()).hasSize(2);
                    assertThat(f.getGenres())
                            .extracting(Genre::id)
                            .containsExactlyInAnyOrder(1, 3);
                });
    }

    @Test
    void update_shouldChangeFieldsAndReplaceGenres() {
        Film saved = filmStorage.save(Film.builder()
                .name("Old").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, null))
                .genres(new LinkedHashSet<>(List.of(new Genre(1, null))))
                .build());

        saved.setName("New");
        saved.setDuration(120);
        saved.setGenres(new LinkedHashSet<>(List.of(new Genre(2, null), new Genre(4, null))));
        filmStorage.update(saved);

        assertThat(filmStorage.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("New");
                    assertThat(f.getDuration()).isEqualTo(120);
                    assertThat(f.getGenres())
                            .extracting(Genre::id)
                            .containsExactlyInAnyOrder(2, 4);
                });
    }

    @Test
    void deleteById_shouldRemoveFilmAndRelatedRows() {
        Film saved = filmStorage.save(Film.builder()
                .name("X").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .genres(new LinkedHashSet<>(List.of(new Genre(1, null))))
                .build());

        filmStorage.deleteById(saved.getId());

        assertThat(filmStorage.findById(saved.getId())).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllFilms() {
        filmStorage.save(Film.builder().name("F1").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1)).duration(100).build());
        filmStorage.save(Film.builder().name("F2").description("d")
                .releaseDate(LocalDate.of(2021, 1, 1)).duration(110).build());

        assertThat(filmStorage.findAll()).hasSize(2);
    }

    @Test
    void findById_shouldReturnEmptyForUnknownId() {
        assertThat(filmStorage.findById(999)).isEmpty();
    }


    @Test
    void addLike_shouldPersistLike() {
        User user = saveUser("u");
        Film film = filmStorage.save(Film.builder()
                .name("F").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .build());

        filmStorage.addLike(film.getId(), user.getId());

        assertThat(filmStorage.findById(film.getId()))
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f.getLikes()).containsExactly(user.getId()));
    }

    @Test
    void removeLike_shouldDeleteLike() {
        User user = saveUser("u");
        Film film = filmStorage.save(Film.builder()
                .name("F").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .build());

        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.removeLike(film.getId(), user.getId());

        assertThat(filmStorage.findById(film.getId()))
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f.getLikes()).isEmpty());
    }

    @Test
    void findPopular_shouldOrderByLikesDesc() {
        User u1 = saveUser("u1");
        User u2 = saveUser("u2");

        Film popular = filmStorage.save(Film.builder()
                .name("Popular").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .build());
        Film unpopular = filmStorage.save(Film.builder()
                .name("Unpopular").description("d")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(110)
                .build());

        filmStorage.addLike(popular.getId(), u1.getId());
        filmStorage.addLike(popular.getId(), u2.getId());
        filmStorage.addLike(unpopular.getId(), u1.getId());

        List<Film> top = filmStorage.findPopular(10);

        assertThat(top).hasSize(2);
        assertThat(top.get(0).getName()).isEqualTo("Popular");
        assertThat(top.get(1).getName()).isEqualTo("Unpopular");
    }

    @Test
    void findPopular_shouldRespectLimit() {
        filmStorage.save(Film.builder().name("F1").description("d")
                .releaseDate(LocalDate.of(2020, 1, 1)).duration(100).build());
        filmStorage.save(Film.builder().name("F2").description("d")
                .releaseDate(LocalDate.of(2021, 1, 1)).duration(110).build());
        filmStorage.save(Film.builder().name("F3").description("d")
                .releaseDate(LocalDate.of(2022, 1, 1)).duration(120).build());

        assertThat(filmStorage.findPopular(2)).hasSize(2);
    }

    @Test
    void findPopular_shouldReturnEmptyWhenNoFilms() {
        assertThat(filmStorage.findPopular(10)).isEmpty();
    }

    private User saveUser(String login) {
        return userStorage.save(User.builder()
                .email(login + "@mail.com")
                .login(login)
                .name(login.toUpperCase())
                .birthday(LocalDate.of(2000, 1, 1))
                .build());
    }
}
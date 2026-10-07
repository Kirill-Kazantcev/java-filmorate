package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.entity.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(GenreDbStorage.class)
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    void findAll_shouldReturnSixItemsOrderedById() {
        List<Genre> all = genreStorage.findAll();

        assertThat(all).hasSize(6);
        assertThat(all).extracting(Genre::id).containsExactly(1, 2, 3, 4, 5, 6);
        assertThat(all).extracting(Genre::name)
                .containsExactly("Комедия", "Драма", "Мультфильм",
                        "Триллер", "Документальный", "Боевик");
    }

    @Test
    void findById_shouldReturnGenre() {
        assertThat(genreStorage.findById(2))
                .isPresent()
                .hasValueSatisfying(g -> {
                    assertThat(g.id()).isEqualTo(2);
                    assertThat(g.name()).isEqualTo("Драма");
                });
    }

    @Test
    void findById_shouldReturnEmptyForUnknownId() {
        assertThat(genreStorage.findById(999)).isEmpty();
    }
}
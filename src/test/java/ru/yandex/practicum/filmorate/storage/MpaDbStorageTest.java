package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.entity.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(MpaDbStorage.class)
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Test
    void findAll_shouldReturnFiveItemsOrderedById() {
        List<Mpa> all = mpaStorage.findAll();

        assertThat(all).hasSize(5);
        assertThat(all).extracting(Mpa::id).containsExactly(1, 2, 3, 4, 5);
        assertThat(all).extracting(Mpa::name)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void findById_shouldReturnMpa() {
        assertThat(mpaStorage.findById(1))
                .isPresent()
                .hasValueSatisfying(m -> {
                    assertThat(m.id()).isEqualTo(1);
                    assertThat(m.name()).isEqualTo("G");
                });
    }

    @Test
    void findById_shouldReturnEmptyForUnknownId() {
        assertThat(mpaStorage.findById(999)).isEmpty();
    }
}
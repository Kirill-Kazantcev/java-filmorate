package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.util.List;
import java.util.Optional;

/**
 * DAO для справочника жанров. Реализация через JdbcTemplate.
 */
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Genre> MAPPER =
            (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name"));

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT id, name FROM genres ORDER BY id", MAPPER);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return jdbc.query("SELECT id, name FROM genres WHERE id = ?", MAPPER, id)
                .stream()
                .findFirst();
    }
}
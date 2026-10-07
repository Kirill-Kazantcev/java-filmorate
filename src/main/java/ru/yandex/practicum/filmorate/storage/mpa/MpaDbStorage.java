package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.entity.Mpa;

import java.util.List;
import java.util.Optional;

/**
 * DAO для справочника MPA. Реализация через JdbcTemplate.
 */
@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Mpa> MAPPER =
            (rs, rowNum) -> new Mpa(rs.getInt("id"), rs.getString("name"));

    @Override
    public List<Mpa> findAll() {
        return jdbc.query("SELECT id, name FROM mpa ORDER BY id", MAPPER);
    }

    @Override
    public Optional<Mpa> findById(Integer id) {
        return jdbc.query("SELECT id, name FROM mpa WHERE id = ?", MAPPER, id)
                .stream()
                .findFirst();
    }
}
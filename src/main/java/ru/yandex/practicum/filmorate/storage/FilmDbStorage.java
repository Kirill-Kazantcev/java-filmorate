package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.entity.Genre;
import ru.yandex.practicum.filmorate.model.entity.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * DAO для фильмов. Реализация через JdbcTemplate.
 */
@Repository("filmDbStorage")
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;

    private static final String BASE_SELECT = """
            SELECT f.id AS film_id, f.name AS film_name, f.description,
                   f.release_date, f.duration,
                   m.id AS mpa_id, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa m ON f.mpa_id = m.id
            """;

    private static final RowMapper<Film> MAPPER = (rs, rowNum) -> {
        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date") != null
                        ? rs.getDate("release_date").toLocalDate() : null)
                .duration(rs.getInt("duration"))
                .build();

        int mpaId = rs.getInt("mpa_id");
        if (!rs.wasNull()) {
            film.setMpa(new Mpa(mpaId, rs.getString("mpa_name")));
        }
        return film;
    };

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null
                    ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().id());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbc.update(
                "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? " +
                        "WHERE id=?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().id() : null,
                film.getId());

        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);
        return film;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            jdbc.update("MERGE INTO film_genres (film_id, genre_id) " +
                            "KEY(film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.id());
        }
    }

    @Override
    public Optional<Film> findById(Integer id) {
        try {
            Film film = jdbc.queryForObject(
                    BASE_SELECT + " WHERE f.id = ?", MAPPER, id);
            film.setGenres(loadGenres(film.getId()));
            film.setLikes(loadLikes(film.getId()));
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbc.query(BASE_SELECT + " ORDER BY f.id", MAPPER);
        films.forEach(f -> {
            f.setGenres(loadGenres(f.getId()));
            f.setLikes(loadLikes(f.getId()));
        });
        return films;
    }

    @Override
    public void deleteById(Integer id) {
        jdbc.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public List<Film> findPopular(int limit) {
        String sql = BASE_SELECT + """
                LEFT JOIN (
                    SELECT film_id, COUNT(*) AS like_count
                    FROM likes
                    GROUP BY film_id
                ) lc ON f.id = lc.film_id
                ORDER BY COALESCE(lc.like_count, 0) DESC, f.id ASC
                LIMIT ?
                """;

        List<Film> films = jdbc.query(sql, MAPPER, limit);
        films.forEach(f -> {
            f.setGenres(loadGenres(f.getId()));
            f.setLikes(loadLikes(f.getId()));
        });
        return films;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        jdbc.update("MERGE INTO likes (film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)",
                filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        jdbc.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    private Set<Genre> loadGenres(Integer filmId) {
        List<Genre> list = jdbc.query(
                "SELECT g.id, g.name FROM genres g " +
                        "JOIN film_genres fg ON g.id = fg.genre_id " +
                        "WHERE fg.film_id = ? ORDER BY g.id",
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                filmId);
        return new LinkedHashSet<>(list);
    }

    private Set<Integer> loadLikes(Integer filmId) {
        List<Integer> ids = jdbc.queryForList(
                "SELECT user_id FROM likes WHERE film_id = ?",
                Integer.class, filmId);
        return new HashSet<>(ids);
    }
}
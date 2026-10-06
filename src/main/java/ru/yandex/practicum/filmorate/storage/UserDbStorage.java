package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.model.entity.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * DAO для пользователей. Реализация через JdbcTemplate.
 * Дружба — односторонняя: строка (user_id, friend_id) означает, что
 * user_id добавил friend_id в свой список.
 */
@Repository("userDbStorage")
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    private static final RowMapper<User> MAPPER = (rs, rowNum) -> User.builder()
            .id(rs.getInt("id"))
            .email(rs.getString("email"))
            .login(rs.getString("login"))
            .name(rs.getString("name"))
            .birthday(rs.getDate("birthday") != null
                    ? rs.getDate("birthday").toLocalDate() : null)
            .build();

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users (email, login, name, birthday) " +
                            "VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, user.getBirthday() != null
                    ? Date.valueOf(user.getBirthday()) : null);
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) {
        jdbc.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null,
                user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT * FROM users WHERE id = ?", MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return jdbc.query("SELECT * FROM users ORDER BY id", MAPPER);
    }

    @Override
    public void deleteById(Integer id) {
        jdbc.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        Integer exists = jdbc.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?",
                Integer.class, userId, friendId);
        if (exists == null || exists == 0) {
            jdbc.update("INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)",
                    userId, friendId);
        }
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        jdbc.update("DELETE FROM friendships WHERE user_id = ? AND friend_id = ?",
                userId, friendId);
    }

    @Override
    public List<User> findFriends(Integer userId) {
        return jdbc.query(
                "SELECT u.* FROM users u " +
                        "JOIN friendships f ON u.id = f.friend_id " +
                        "WHERE f.user_id = ? " +
                        "ORDER BY u.id",
                MAPPER, userId);
    }

    @Override
    public List<User> findCommonFriends(Integer userId, Integer otherId) {
        return jdbc.query(
                "SELECT u.* FROM users u " +
                        "WHERE u.id IN ( " +
                        "    SELECT friend_id FROM friendships WHERE user_id = ? " +
                        "    INTERSECT " +
                        "    SELECT friend_id FROM friendships WHERE user_id = ? " +
                        ") ORDER BY u.id",
                MAPPER, userId, otherId);
    }
}
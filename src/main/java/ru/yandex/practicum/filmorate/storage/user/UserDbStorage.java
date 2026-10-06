package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.entity.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * DAO для пользователей. Реализация через JdbcTemplate.
 * Дружба — односторонняя: строка (user_id, friend_id) означает, что
 * user_id добавил friend_id в свой список.
 */
@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    private static final String INSERT_USER =
            "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER =
            "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
    private static final String SELECT_USER_BY_ID =
            "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_ALL_USERS =
            "SELECT * FROM users ORDER BY id";
    private static final String DELETE_USER =
            "DELETE FROM users WHERE id = ?";
    private static final String MERGE_FRIENDSHIP =
            "MERGE INTO friendships (user_id, friend_id) KEY(user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIENDSHIP =
            "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String SELECT_FRIENDS =
            "SELECT u.* FROM users u " +
                    "JOIN friendships f ON u.id = f.friend_id " +
                    "WHERE f.user_id = ? ORDER BY u.id";
    private static final String SELECT_COMMON_FRIENDS =
            "SELECT u.* FROM users u WHERE u.id IN ( " +
                    "    SELECT friend_id FROM friendships WHERE user_id = ? " +
                    "    INTERSECT " +
                    "    SELECT friend_id FROM friendships WHERE user_id = ? " +
                    ") ORDER BY u.id";

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
            PreparedStatement ps = con.prepareStatement(INSERT_USER,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, user.getBirthday() != null
                    ? Date.valueOf(user.getBirthday()) : null);
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        jdbc.update(UPDATE_USER,
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
                    jdbc.queryForObject(SELECT_USER_BY_ID, MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return jdbc.query(SELECT_ALL_USERS, MAPPER);
    }

    @Override
    public void deleteById(Integer id) {
        jdbc.update(DELETE_USER, id);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbc.update(MERGE_FRIENDSHIP, userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        jdbc.update(DELETE_FRIENDSHIP, userId, friendId);
    }

    @Override
    public List<User> findFriends(Integer userId) {
        return jdbc.query(SELECT_FRIENDS, MAPPER, userId);
    }

    @Override
    public List<User> findCommonFriends(Integer userId, Integer otherId) {
        return jdbc.query(SELECT_COMMON_FRIENDS, MAPPER, userId, otherId);
    }
}
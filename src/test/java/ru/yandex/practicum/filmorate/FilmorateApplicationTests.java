package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FilmorateApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void schemaAndDataAreInitialized() {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mpa", Integer.class))
                .isEqualTo(5);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM genres", Integer.class))
                .isEqualTo(6);

        var tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME",
                String.class);
        assertThat(tables).contains(
                "FILMS", "FILM_GENRES", "FRIENDSHIPS",
                "GENRES", "LIKES", "MPA", "USERS");
    }
}
package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.entity.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserDbStorage.class)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void save_shouldAssignIdAndPersist() {
        User user = User.builder()
                .email("alice@mail.com")
                .login("alice")
                .name("Alice")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User saved = userStorage.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(userStorage.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getEmail()).isEqualTo("alice@mail.com");
                    assertThat(u.getLogin()).isEqualTo("alice");
                    assertThat(u.getName()).isEqualTo("Alice");
                    assertThat(u.getBirthday()).isEqualTo(LocalDate.of(2000, 1, 1));
                });
    }

    @Test
    void update_shouldChangeFields() {
        User saved = userStorage.save(User.builder()
                .email("bob@mail.com").login("bob").name("Bob")
                .birthday(LocalDate.of(1995, 5, 5)).build());

        saved.setName("Robert");
        saved.setEmail("robert@mail.com");
        userStorage.update(saved);

        assertThat(userStorage.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getName()).isEqualTo("Robert");
                    assertThat(u.getEmail()).isEqualTo("robert@mail.com");
                });
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userStorage.save(User.builder().email("a@b.c").login("u1").name("U1")
                .birthday(LocalDate.of(2000, 1, 1)).build());
        userStorage.save(User.builder().email("b@b.c").login("u2").name("U2")
                .birthday(LocalDate.of(2000, 1, 1)).build());

        assertThat(userStorage.findAll()).hasSize(2);
    }

    @Test
    void deleteById_shouldRemoveUser() {
        User saved = userStorage.save(User.builder()
                .email("a@b.c").login("u1").name("U1")
                .birthday(LocalDate.of(2000, 1, 1)).build());

        userStorage.deleteById(saved.getId());

        assertThat(userStorage.findById(saved.getId())).isEmpty();
    }

    @Test
    void findById_shouldReturnEmptyForUnknownId() {
        assertThat(userStorage.findById(999)).isEmpty();
    }

    @Test
    void addFriend_shouldBeOneDirectional() {
        User a = userStorage.save(User.builder().email("a@b.c").login("a").name("A")
                .birthday(LocalDate.of(2000, 1, 1)).build());
        User b = userStorage.save(User.builder().email("b@b.c").login("b").name("B")
                .birthday(LocalDate.of(2000, 1, 1)).build());

        userStorage.addFriend(a.getId(), b.getId());

        assertThat(userStorage.findFriends(a.getId()))
                .extracting(User::getId).containsExactly(b.getId());
        assertThat(userStorage.findFriends(b.getId())).isEmpty();
    }

    @Test
    void addFriend_shouldBeIdempotent() {
        User a = userStorage.save(User.builder().email("a@b.c").login("a").name("A")
                .birthday(LocalDate.of(2000, 1, 1)).build());
        User b = userStorage.save(User.builder().email("b@b.c").login("b").name("B")
                .birthday(LocalDate.of(2000, 1, 1)).build());

        userStorage.addFriend(a.getId(), b.getId());
        userStorage.addFriend(a.getId(), b.getId());

        assertThat(userStorage.findFriends(a.getId())).hasSize(1);
    }

    @Test
    void removeFriend_shouldDeleteFriendship() {
        User a = userStorage.save(User.builder().email("a@b.c").login("a").name("A")
                .birthday(LocalDate.of(2000, 1, 1)).build());
        User b = userStorage.save(User.builder().email("b@b.c").login("b").name("B")
                .birthday(LocalDate.of(2000, 1, 1)).build());

        userStorage.addFriend(a.getId(), b.getId());
        userStorage.removeFriend(a.getId(), b.getId());

        assertThat(userStorage.findFriends(a.getId())).isEmpty();
    }

    @Test
    void findFriends_shouldReturnEmptyList() {
        User a = userStorage.save(User.builder().email("a@b.c").login("a").name("A")
                .birthday(LocalDate.of(2000, 1, 1)).build());

        assertThat(userStorage.findFriends(a.getId())).isEmpty();
    }

    @Test
    void findCommonFriends_shouldReturnIntersection() {
        User a = userStorage.save(User.builder().email("a@b.c").login("a").name("A")
                .birthday(LocalDate.of(2000, 1, 1)).build());
        User b = userStorage.save(User.builder().email("b@b.c").login("b").name("B")
                .birthday(LocalDate.of(2000, 1, 1)).build());
        User c = userStorage.save(User.builder().email("c@b.c").login("c").name("C")
                .birthday(LocalDate.of(2000, 1, 1)).build());
        User d = userStorage.save(User.builder().email("d@b.c").login("d").name("D")
                .birthday(LocalDate.of(2000, 1, 1)).build());

        userStorage.addFriend(a.getId(), c.getId());
        userStorage.addFriend(a.getId(), d.getId());
        userStorage.addFriend(b.getId(), c.getId());

        assertThat(userStorage.findCommonFriends(a.getId(), b.getId()))
                .extracting(User::getId).containsExactly(c.getId());
    }

    @Test
    void findCommonFriends_shouldReturnEmptyWhenNoCommon() {
        User a = userStorage.save(User.builder().email("a@b.c").login("a").name("A")
                .birthday(LocalDate.of(2000, 1, 1)).build());
        User b = userStorage.save(User.builder().email("b@b.c").login("b").name("B")
                .birthday(LocalDate.of(2000, 1, 1)).build());

        assertThat(userStorage.findCommonFriends(a.getId(), b.getId())).isEmpty();
    }
}
package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.model.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1)
                .email("user1@test.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();

        user2 = User.builder()
                .id(2)
                .email("user2@test.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1991, 2, 2))
                .friends(new HashSet<>())
                .build();

        user3 = User.builder()
                .id(3)
                .email("user3@test.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(1992, 3, 3))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    void addFriend_ShouldAddBothUsersToEachOtherFriends() {
        when(userStorage.findById(1)).thenReturn(Optional.of(user1));
        when(userStorage.findById(2)).thenReturn(Optional.of(user2));
        when(userStorage.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.addFriend(1, 2);

        assertTrue(user1.getFriends().contains(2));
        assertTrue(user2.getFriends().contains(1));
        verify(userStorage, times(2)).update(any(User.class));
    }

    @Test
    void addFriend_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userStorage.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addFriend(1, 2));
        verify(userStorage, never()).update(any(User.class));
    }

    @Test
    void addFriend_ShouldThrowValidationException_WhenAddingSelf() {
        when(userStorage.findById(1)).thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> userService.addFriend(1, 1));
        verify(userStorage, never()).update(any(User.class));
    }

    @Test
    void addFriend_ShouldNotThrowException_WhenUsersAlreadyFriends() {
        user1.getFriends().add(2);
        user2.getFriends().add(1);

        when(userStorage.findById(1)).thenReturn(Optional.of(user1));
        when(userStorage.findById(2)).thenReturn(Optional.of(user2));

        assertDoesNotThrow(() -> userService.addFriend(1, 2));
        verify(userStorage, never()).update(any(User.class));
    }

    @Test
    void removeFriend_ShouldRemoveBothUsersFromEachOtherFriends() {
        user1.getFriends().add(2);
        user2.getFriends().add(1);

        when(userStorage.findById(1)).thenReturn(Optional.of(user1));
        when(userStorage.findById(2)).thenReturn(Optional.of(user2));
        when(userStorage.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.removeFriend(1, 2);

        assertFalse(user1.getFriends().contains(2));
        assertFalse(user2.getFriends().contains(1));
        verify(userStorage, times(2)).update(any(User.class));
    }

    @Test
    void removeFriend_ShouldNotThrowException_WhenUsersNotFriends() {
        when(userStorage.findById(1)).thenReturn(Optional.of(user1));
        when(userStorage.findById(2)).thenReturn(Optional.of(user2));

        assertDoesNotThrow(() -> userService.removeFriend(1, 2));
        verify(userStorage, never()).update(any(User.class));
    }

    @Test
    void getFriends_ShouldReturnListOfFriends() {
        user1.getFriends().add(2);
        user1.getFriends().add(3);

        UserDto userDto2 = new UserDto(2, "user2@test.com", "user2", "User Two", LocalDate.of(1991, 2, 2));
        UserDto userDto3 = new UserDto(3, "user3@test.com", "user3", "User Three", LocalDate.of(1992, 3, 3));

        when(userStorage.findById(1)).thenReturn(Optional.of(user1));
        when(userStorage.findById(2)).thenReturn(Optional.of(user2));
        when(userStorage.findById(3)).thenReturn(Optional.of(user3));
        when(userMapper.toDto(user2)).thenReturn(userDto2);
        when(userMapper.toDto(user3)).thenReturn(userDto3);

        List<UserDto> friends = userService.getFriends(1);

        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(f -> f.id().equals(2)));
        assertTrue(friends.stream().anyMatch(f -> f.id().equals(3)));
    }

    @Test
    void getFriends_ShouldReturnEmptyList_WhenNoFriends() {
        when(userStorage.findById(1)).thenReturn(Optional.of(user1));

        List<UserDto> friends = userService.getFriends(1);

        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends_ShouldReturnCommonFriends() {
        user1.getFriends().add(3);
        user2.getFriends().add(3);
        user2.getFriends().add(4);

        UserDto userDto3 = new UserDto(3, "user3@test.com", "user3", "User Three", LocalDate.of(1992, 3, 3));

        when(userStorage.findById(1)).thenReturn(Optional.of(user1));
        when(userStorage.findById(2)).thenReturn(Optional.of(user2));
        when(userStorage.findById(3)).thenReturn(Optional.of(user3));
        when(userMapper.toDto(user3)).thenReturn(userDto3);

        List<UserDto> commonFriends = userService.getCommonFriends(1, 2);

        assertEquals(1, commonFriends.size());
        assertEquals(3, commonFriends.getFirst().id());
    }

    @Test
    void getCommonFriends_ShouldReturnEmptyList_WhenNoCommonFriends() {
        user1.getFriends().add(3);
        user2.getFriends().add(4);

        when(userStorage.findById(1)).thenReturn(Optional.of(user1));
        when(userStorage.findById(2)).thenReturn(Optional.of(user2));

        List<UserDto> commonFriends = userService.getCommonFriends(1, 2);

        assertTrue(commonFriends.isEmpty());
    }
}
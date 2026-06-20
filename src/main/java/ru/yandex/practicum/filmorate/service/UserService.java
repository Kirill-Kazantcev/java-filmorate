package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.model.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserService implements UserServiceInterface {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Создание пользователя: {}", userDto.login());
        User user = userMapper.toEntity(userDto);
        normalizeName(user);
        User saved = userStorage.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto update(UserDto userDto) {
        log.debug("Обновление пользователя: id={}", userDto.id());
        getUserById(userDto.id());
        User user = userMapper.toEntity(userDto);
        normalizeName(user);
        User updated = userStorage.update(user);
        return userMapper.toDto(updated);
    }

    @Override
    public UserDto findById(Integer id) {
        User user = getUserById(id);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        log.debug("Запрос всех пользователей");
        return userStorage.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        log.debug("Удаление пользователя: id={}", id);
        getUserById(id);
        userStorage.deleteById(id);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        log.debug("Добавление в друзья: userId={}, friendId={}", userId, friendId);

        checkUsersNotSame(userId, friendId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.debug("Пользователи уже являются друзьями: {} <-> {}", userId, friendId);
            return;
        }

        boolean userAdded = user.getFriends().add(friendId);
        boolean friendAdded = friend.getFriends().add(userId);

        if (userAdded || friendAdded) {
            userStorage.update(user);
            userStorage.update(friend);
            log.info("Пользователи стали друзьями: {} <-> {}", userId, friendId);
        }
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        log.debug("Удаление из друзей: userId={}, friendId={}", userId, friendId);

        checkUsersNotSame(userId, friendId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        boolean userRemoved = user.getFriends().remove(friendId);
        boolean friendRemoved = friend.getFriends().remove(userId);

        if (userRemoved || friendRemoved) {
            userStorage.update(user);
            userStorage.update(friend);
            log.info("Пользователи перестали быть друзьями: {} <-> {}", userId, friendId);
        } else {
            log.debug("Пользователи не являются друзьями (удаление игнорируется): {} <-> {}", userId, friendId);
        }
    }

    @Override
    public List<UserDto> getFriends(Integer userId) {
        log.debug("Получение списка друзей: userId={}", userId);

        User user = getUserById(userId);

        if (user.getFriends().isEmpty()) {
            log.debug("У пользователя нет друзей: userId={}", userId);
            return List.of();
        }

        return user.getFriends().stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getCommonFriends(Integer userId, Integer otherId) {
        log.debug("Получение общих друзей: userId={}, otherId={}", userId, otherId);

        checkUsersNotSame(userId, otherId);

        User user = getUserById(userId);
        User other = getUserById(otherId);

        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(other.getFriends());

        if (commonFriends.isEmpty()) {
            log.debug("Нет общих друзей у пользователей: {} и {}", userId, otherId);
            return List.of();
        }

        return commonFriends.stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private void normalizeName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя заменено на логин: {}", user.getLogin());
        }
    }

    private User getUserById(Integer id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
    private void checkUsersNotSame(Integer userId, Integer otherId) {
        if (userId.equals(otherId)) {
            log.warn("Попытка операции с самим собой: userId={}", userId);
            throw new ValidationException("Нельзя выполнить операцию над самим собой");
        }
    }
}
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

import java.util.List;

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
        if (userDto.id() == null) {
            throw new ValidationException("id пользователя обязателен для обновления");
        }
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
                .toList();
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
        getUserById(userId);
        getUserById(friendId);

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        log.debug("Удаление из друзей: userId={}, friendId={}", userId, friendId);

        checkUsersNotSame(userId, friendId);
        getUserById(userId);
        getUserById(friendId);

        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    @Override
    public List<UserDto> getFriends(Integer userId) {
        log.debug("Получение списка друзей: userId={}", userId);
        getUserById(userId);

        return userStorage.findFriends(userId).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getCommonFriends(Integer userId, Integer otherId) {
        log.debug("Получение общих друзей: userId={}, otherId={}", userId, otherId);

        checkUsersNotSame(userId, otherId);
        getUserById(userId);
        getUserById(otherId);

        return userStorage.findCommonFriends(userId, otherId).stream()
                .map(userMapper::toDto)
                .toList();
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
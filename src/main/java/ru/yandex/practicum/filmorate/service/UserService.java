package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        userStorage.findById(userDto.id())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userDto.id() + " не найден"));
        User user = userMapper.toEntity(userDto);
        normalizeName(user);
        User updated = userStorage.update(user);
        return userMapper.toDto(updated);
    }

    @Override
    public UserDto findById(Integer id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
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
        userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        userStorage.deleteById(id);
    }

    private void normalizeName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя заменено на логин: {}", user.getLogin());
        }
    }
}
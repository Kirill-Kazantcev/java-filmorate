package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.dto.UserDto;

import java.util.List;

/**
 * Интерфейс сервиса для работы с пользователями.
 */
public interface UserServiceInterface {

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto findById(Integer id);

    List<UserDto> findAll();

    void delete(Integer id);
}
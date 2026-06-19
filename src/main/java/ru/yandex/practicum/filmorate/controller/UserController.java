package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserServiceInterface;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {

    private final UserServiceInterface userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя: {}", userDto.login());
        return userService.create(userDto);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя: id={}", userDto.id());
        return userService.update(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        log.info("Запрос на получение пользователя: id={}", id);
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        log.info("Запрос на удаление пользователя: id={}", id);
        userService.delete(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Запрос на добавление в друзья: userId={}, friendId={}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Запрос на удаление из друзей: userId={}, friendId={}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable Integer id) {
        log.info("Запрос на получение списка друзей: userId={}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Запрос на получение общих друзей: userId={}, otherId={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
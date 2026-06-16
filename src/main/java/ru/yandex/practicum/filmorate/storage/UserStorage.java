package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    User update(User user);

    Optional<User> findById(Integer id);

    List<User> findAll();

    void deleteById(Integer id);
}
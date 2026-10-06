package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    User update(User user);

    Optional<User> findById(Integer id);

    List<User> findAll();

    void deleteById(Integer id);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    List<User> findFriends(Integer userId);

    List<User> findCommonFriends(Integer userId, Integer otherId);
}
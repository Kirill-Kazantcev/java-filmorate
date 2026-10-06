package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.entity.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component("inMemoryUserStorage")
@SuppressWarnings("unused")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public User save(User user) {
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), user);
        log.debug("Сохранен пользователь: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.debug("Обновлен пользователь: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Integer id) {
        users.remove(id);
        log.debug("Удален пользователь: id={}", id);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        if (user != null) {
            user.getFriends().add(friendId);
            log.debug("Друг добавлен: userId={}, friendId={}", userId, friendId);
        }
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        if (user != null) {
            user.getFriends().remove(friendId);
            log.debug("Друг удалён: userId={}, friendId={}", userId, friendId);
        }
    }

    @Override
    public List<User> findFriends(Integer userId) {
        User user = users.get(userId);
        if (user == null || user.getFriends().isEmpty()) {
            return List.of();
        }
        List<User> result = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            User friend = users.get(friendId);
            if (friend != null) {
                result.add(friend);
            }
        }
        return result;
    }

    @Override
    public List<User> findCommonFriends(Integer userId, Integer otherId) {
        User user = users.get(userId);
        User other = users.get(otherId);
        if (user == null || other == null) {
            return List.of();
        }
        Set<Integer> common = new HashSet<>(user.getFriends());
        common.retainAll(other.getFriends());

        List<User> result = new ArrayList<>();
        for (Integer id : common) {
            User u = users.get(id);
            if (u != null) {
                result.add(u);
            }
        }
        return result;
    }
}
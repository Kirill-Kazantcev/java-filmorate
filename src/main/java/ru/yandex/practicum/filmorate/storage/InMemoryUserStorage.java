package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
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
}
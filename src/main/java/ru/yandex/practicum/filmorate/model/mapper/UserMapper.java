package ru.yandex.practicum.filmorate.model.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.model.entity.User;

/**
 * Маппер для преобразования User <-> UserDto.
 */
@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return User.builder()
                .id(userDto.id())
                .email(userDto.email())
                .login(userDto.login())
                .name(userDto.name())
                .birthday(userDto.birthday())
                .build();
    }
}
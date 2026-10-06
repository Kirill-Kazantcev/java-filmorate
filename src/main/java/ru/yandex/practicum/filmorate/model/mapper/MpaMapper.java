package ru.yandex.practicum.filmorate.model.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.entity.Mpa;

/**
 * Маппер для Mpa <-> MpaDto.
 */
@Component
public class MpaMapper {

    public MpaDto toDto(Mpa mpa) {
        if (mpa == null) {
            return null;
        }
        return new MpaDto(mpa.id(), mpa.name());
    }

    public Mpa toEntity(MpaDto dto) {
        if (dto == null) {
            return null;
        }
        return new Mpa(dto.id(), dto.name());
    }
}
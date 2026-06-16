package ru.yandex.practicum.filmorate.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class DateValidator {
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public static boolean isBeforeMinReleaseDate(LocalDate date) {
        return date != null && date.isBefore(MIN_RELEASE_DATE);
    }
}
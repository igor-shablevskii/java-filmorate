package ru.yandex.practicum.filmorate.model;

import org.springframework.core.convert.converter.Converter;

public class FilmSortEnumConverter implements Converter<String, FilmSortBy> {
    @Override
    public FilmSortBy convert(String source) {
        return FilmSortBy.valueOf(source.toUpperCase());
    }
}
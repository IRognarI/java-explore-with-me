package ru.practicum.ewmService.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CategoryDto {
    private long id;
    @Setter
    private String name;
}
package ru.practicum.ewmService.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.category.dto.NewCategoryDto;
import ru.practicum.ewmService.category.model.Category;

@UtilityClass
public class CategoryMapper {

    public Category toCategory(NewCategoryDto dto) {
        return new Category(null, dto.getName());
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
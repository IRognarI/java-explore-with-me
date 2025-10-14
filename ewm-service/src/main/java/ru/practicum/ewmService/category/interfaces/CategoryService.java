package ru.practicum.ewmService.category.interfaces;

import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto dto);

    CategoryDto updateCategory(long catId, NewCategoryDto dto);

    void deleteCategory(long catId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
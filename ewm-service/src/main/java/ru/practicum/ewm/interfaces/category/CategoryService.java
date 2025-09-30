package ru.practicum.ewm.interfaces.category;

import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.ewm.model.category.Category;

public interface CategoryService {

    Category addCategory(CategoryDto categoryDto);

    Category updateCategory(Long catId, String name);

    void removeCategory(Long catId);
}

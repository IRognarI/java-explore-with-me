package ru.practicum.ewm.interfaces.category;

import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.ewm.model.category.Category;

import java.util.List;

public interface CategoryService {

    Category addCategory(CategoryDto categoryDto);

    Category updateCategory(Long catId, String name);

    void removeCategory(Long catId);

    List<Category> getCategories(Integer from, Integer size);

    Category getCategory(Long catId);
}

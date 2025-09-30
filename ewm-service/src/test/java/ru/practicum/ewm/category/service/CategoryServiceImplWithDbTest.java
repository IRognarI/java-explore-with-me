package ru.practicum.ewm.category.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.ewm.exception.ObjectDuplicatedException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.service.category.CategoryServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
public class CategoryServiceImplWithDbTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    private CategoryDto categoryDto_1;
    private CategoryDto categoryDto_2;
    private CategoryDto categoryDto_3;
    private CategoryDto categoryDto_4;

    @BeforeEach
    public void setUp() {
        categoryDto_1 = CategoryDto.builder().name("category#1").build();
        categoryDto_2 = CategoryDto.builder().name("category#2").build();
        categoryDto_3 = CategoryDto.builder().name(categoryDto_1.getName()).build();
        categoryDto_4 = CategoryDto.builder().name("category#4").build();
    }

    @Test
    public void addCategory_shouldThrowWhenCategoryExists() {
        categoryService.addCategory(categoryDto_1);
        categoryService.addCategory(categoryDto_2);

        Assertions.assertThrows(ObjectDuplicatedException.class,
                () -> categoryService.addCategory(categoryDto_3));
    }

    @Test
    public void addCategory_shouldReturnEntityFromData() {
        Category targetCategory = categoryService.addCategory(categoryDto_1);

        Assertions.assertEquals(categoryDto_1.getName(), targetCategory.getName());
        Assertions.assertNotNull(targetCategory.getId(), "ID не был добавлен");
    }

    @Test
    public void updateCategory_Correct() {
        String newName = "categoryUpdate";

        Category category = categoryService.addCategory(categoryDto_1);
        Category updateCategory = categoryService.updateCategory(category.getId(), newName);

        Assertions.assertEquals(updateCategory.getName(), newName);
        Assertions.assertEquals(updateCategory.getId(), category.getId());
    }

    @Test
    public void updateCategory_shouldThrowWhenCategoryNameExists() {
        Category category = categoryService.addCategory(categoryDto_1);

        String existsName = category.getName();

        Assertions.assertThrows(ObjectDuplicatedException.class,
                () -> categoryService.updateCategory(category.getId(), existsName));
    }

    @Test
    public void updateCategory_shouldThrowWhenCategoryIdIsNull() {
        Assertions.assertThrows(ValidationException.class,
                () -> categoryService.updateCategory(null, "Name"));
    }

    @Test
    public void updateCategory_shouldThrowWhenCategoryIdIsNegative() {
        Assertions.assertThrows(ValidationException.class,
                () -> categoryService.updateCategory(-3L, "Name"));
    }

    @Test
    public void updateCategory_shouldThrowWhenNameIsBlank() {
        Category category = categoryService.addCategory(categoryDto_1);

        Assertions.assertThrows(ValidationException.class,
                () -> categoryService.updateCategory(category.getId(), ""));
    }

    @Test
    public void removeCategory_Correct() {
        Category category = categoryService.addCategory(categoryDto_1);

        Assertions.assertNotNull(category.getId(), "Категория не была создана");

        categoryService.removeCategory(category.getId());

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> categoryService.updateCategory(category.getId(), categoryDto_2.getName()));
    }
}

package ru.practicum.ewm.category.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.category.JpaCategoryRepository;
import ru.practicum.ewm.service.category.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private JpaCategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category_1;

    private CategoryDto categoryDto_1;

    @BeforeEach
    public void setUp() {
        categoryDto_1 = CategoryDto.builder()
                .name("category#1")
                .build();

        category_1 = Category.builder()
                .id(1l)
                .name(categoryDto_1.getName())
                .build();

        Mockito.lenient().when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(category_1);
    }

    @Test
    public void addCategory_Correct() {
        Category targetCategory = categoryService.addCategory(categoryDto_1);

        Assertions.assertEquals(categoryDto_1.getName(), targetCategory.getName());
        Assertions.assertTrue(targetCategory.getId() != null, "Категории не был присвоен ID");
    }

    @Test
    public void removeCategory_shouldThrowWhenCategoryNotFound() {
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> categoryService.removeCategory(4L));
    }
}

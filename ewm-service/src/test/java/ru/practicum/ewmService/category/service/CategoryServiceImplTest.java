package ru.practicum.ewmService.category.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.category.dto.NewCategoryDto;
import ru.practicum.ewmService.category.mapper.CategoryMapper;
import ru.practicum.ewmService.category.model.Category;
import ru.practicum.ewmService.category.repository.CategoryRepository;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void addCategory_ShouldSaveAndReturnCategoryDto_WhenNameIsUnique() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Test Category");
        Category category = Category.builder().id(1L).name("Test Category").build();
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category);

        when(categoryRepository.existsByName(newCategoryDto.getName())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.addCategory(newCategoryDto);

        assertEquals(categoryDto, result);
        verify(categoryRepository, times(1)).existsByName(newCategoryDto.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void addCategory_ShouldThrowIntegrityException_WhenNameIsNotUnique() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Duplicate Category");

        when(categoryRepository.existsByName(newCategoryDto.getName())).thenReturn(true);

        assertThrows(IntegrityException.class, () -> categoryService.addCategory(newCategoryDto));
        verify(categoryRepository, times(1)).existsByName(newCategoryDto.getName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateAndReturnCategoryDto_WhenNameIsUniqueAndDifferent() {
        long catId = 1L;
        NewCategoryDto updateDto = new NewCategoryDto("Updated Category");
        Category existingCategory = Category.builder().id(catId).name("Old Category").build();
        Category updatedCategory = Category.builder().id(catId).name("Updated Category").build();
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(updatedCategory);

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(updateDto.getName())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryDto result = categoryService.updateCategory(catId, updateDto);

        assertEquals(categoryDto, result);
        verify(categoryRepository, times(1)).findById(catId);
        verify(categoryRepository, times(1)).existsByName(updateDto.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowNotFoundException_WhenCategoryNotFound() {
        long catId = 1L;
        NewCategoryDto updateDto = new NewCategoryDto("Updated Category");

        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(catId, updateDto));
        verify(categoryRepository, times(1)).findById(catId);
        verify(categoryRepository, never()).existsByName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowIntegrityException_WhenNameIsNotUnique() {
        long catId = 1L;
        NewCategoryDto updateDto = new NewCategoryDto("Duplicate Category");
        Category existingCategory = Category.builder().id(catId).name("Old Category").build();

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(updateDto.getName())).thenReturn(true);

        assertThrows(IntegrityException.class, () -> categoryService.updateCategory(catId, updateDto));
        verify(categoryRepository, times(1)).findById(catId);
        verify(categoryRepository, times(1)).existsByName(updateDto.getName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldReturnSameCategoryDto_WhenNameIsNull() {
        long catId = 1L;
        NewCategoryDto updateDto = new NewCategoryDto(null);
        Category existingCategory = Category.builder().id(catId).name("Old Category").build();
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(existingCategory);

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(existingCategory));

        CategoryDto result = categoryService.updateCategory(catId, updateDto);

        assertEquals(categoryDto, result);
        verify(categoryRepository, times(1)).findById(catId);
        verify(categoryRepository, never()).existsByName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldReturnSameCategoryDto_WhenNameIsSame() {
        long catId = 1L;
        NewCategoryDto updateDto = new NewCategoryDto("Old Category");
        Category existingCategory = Category.builder().id(catId).name("Old Category").build();
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(existingCategory);

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(existingCategory));

        CategoryDto result = categoryService.updateCategory(catId, updateDto);

        assertEquals(categoryDto, result);
        verify(categoryRepository, times(1)).findById(catId);
        verify(categoryRepository, never()).existsByName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteCategory_WhenCategoryExistsAndIsEmpty() {
        long catId = 1L;
        Category category = Category.builder().id(catId).name("Test Category").build();

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(category));
        when(eventRepository.existsByCategoryId(catId)).thenReturn(false);

        assertDoesNotThrow(() -> categoryService.deleteCategory(catId));
        verify(categoryRepository, times(1)).findById(catId);
        verify(eventRepository, times(1)).existsByCategoryId(catId);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_ShouldThrowNotFoundException_WhenCategoryNotFound() {
        long catId = 1L;

        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(catId));
        verify(categoryRepository, times(1)).findById(catId);
        verify(eventRepository, never()).existsByCategoryId(anyLong());
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldThrowIntegrityException_WhenCategoryIsNotEmpty() {
        long catId = 1L;
        Category category = Category.builder().id(catId).name("Test Category").build();

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(category));
        when(eventRepository.existsByCategoryId(catId)).thenReturn(true);

        assertThrows(IntegrityException.class, () -> categoryService.deleteCategory(catId));
        verify(categoryRepository, times(1)).findById(catId);
        verify(eventRepository, times(1)).existsByCategoryId(catId);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void getCategories_ShouldReturnListOfCategoryDtos() {
        int from = 0;
        int size = 10;
        List<Category> categories = Stream.of(
                Category.builder().id(1L).name("Category 1").build(),
                Category.builder().id(2L).name("Category 2").build()
        ).collect(Collectors.toList());
        Page<Category> page = new PageImpl<>(categories);
        List<CategoryDto> categoryDtos = categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        when(categoryRepository.findAll(PageRequest.of(from, size))).thenReturn(page);

        List<CategoryDto> result = categoryService.getCategories(from, size);

        assertEquals(categoryDtos, result);
        verify(categoryRepository, times(1)).findAll(PageRequest.of(from, size));
    }

    @Test
    void getCategory_ShouldReturnCategoryDto_WhenCategoryExists() {
        long catId = 1L;
        Category category = Category.builder().id(catId).name("Test Category").build();
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category);

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.getCategory(catId);

        assertEquals(categoryDto, result);
        verify(categoryRepository, times(1)).findById(catId);
    }

    @Test
    void getCategory_ShouldThrowNotFoundException_WhenCategoryNotFound() {
        long catId = 1L;

        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategory(catId));
        verify(categoryRepository, times(1)).findById(catId);
    }
}
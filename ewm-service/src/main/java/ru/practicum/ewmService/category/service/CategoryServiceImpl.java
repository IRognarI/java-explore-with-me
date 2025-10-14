package ru.practicum.ewmService.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.category.dto.NewCategoryDto;
import ru.practicum.ewmService.category.interfaces.CategoryService;
import ru.practicum.ewmService.category.mapper.CategoryMapper;
import ru.practicum.ewmService.category.model.Category;
import ru.practicum.ewmService.category.repository.CategoryRepository;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto dto) {

        log.info("Add category ADMIN POST request: {}", dto);
        if (categoryRepository.existsByName(dto.getName())) {
            throw new IntegrityException("Duplicated category name '%s'".formatted(dto.getName()));
        }
        Category category = CategoryMapper.toCategory(dto);
        category = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(long catId, NewCategoryDto dto) {

        log.info("Update category admin POST request: {}", dto);
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with id=%d not found".formatted(catId)));
        if (dto.getName() != null && !Objects.equals(dto.getName(), category.getName())) {
            if (categoryRepository.existsByName(dto.getName())) {
                throw new IntegrityException("Duplicated category name '%s'".formatted(dto.getName()));
            }
            category.setName(dto.getName());
            category = categoryRepository.save(category);
        }
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(long catId) {

        log.info("Delete category admin POST request: {}", catId);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Category with id=%d not found".formatted(catId)));
        if (eventRepository.existsByCategoryId(catId)) {
            throw new IntegrityException("Category id=%d is not empty".formatted(catId));
        }
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {

        log.info("Get category list public GET request: from={}, size={}", from, size);
        var cats = categoryRepository.findAll(PageRequest.of(from, size));
        return cats.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(long catId) {

        log.info("Get category public GET request: {}", catId);
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with id=%d not found".formatted(catId)));
        return CategoryMapper.toCategoryDto(category);
    }
}
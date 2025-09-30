package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.ewm.exception.ObjectDuplicatedException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.interfaces.category.CategoryService;
import ru.practicum.ewm.mapper.Mapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.category.JpaCategoryRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final JpaCategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category addCategory(CategoryDto categoryDto) {
        Optional<Category> categoryExists = Optional.ofNullable(
                categoryRepository.getCategoryByName(categoryDto.getName())
        );

        if (categoryExists.isPresent()) {
            throw new ObjectDuplicatedException("Категория: " + categoryDto.getName() + " - уже добавлена");
        }

        categoryExists = Optional.of(Mapper.dtoToCategory(categoryDto));

        Category category = categoryRepository.save(categoryExists.get());

        LOG.info("Добавлена категория: {}", category);

        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long catId, String name) {
        if (catId == null || catId < 1) {
            throw new ValidationException("ID не может быть " + catId);
        }

        if (name == null || name.isBlank()) {
            throw new ValidationException("Укажите корректное название категории");
        }

        Optional<Category> targetCategory = Optional.ofNullable(categoryRepository.getCategoryById(catId));

        if (targetCategory.isEmpty()) {
            throw new ObjectNotFoundException("Категория с ID { " + catId + " } - не найдена");
        }

        boolean categoryNameExists = categoryRepository.categoryExists(name);

        if (categoryNameExists) {
            throw new ObjectDuplicatedException("Категория " + name + " - уже добавлена");
        }

        Category oldCategory = targetCategory.get().toBuilder().build();

        Category updateCategory = targetCategory.get()
                .toBuilder()
                .name(name)
                .build();

        categoryRepository.save(updateCategory);

        LOG.info("Категория обновлена!\nБыло: {}\nСтало: {}", oldCategory, updateCategory);

        return updateCategory;
    }

    @Override
    @Transactional
    public void removeCategory(Long catId) {
        if (catId == null || catId < 1) {
            throw new ValidationException("ID категории не может быть " + catId);
        }

        boolean categoryExists = categoryRepository.existsById(catId);

        if (!categoryExists) throw new ObjectNotFoundException("Категория с ID { " + catId + " } - не найдена");

        categoryRepository.deleteById(catId);   
    }
}

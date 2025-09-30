package ru.practicum.ewm.controller.admin.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.ewm.interfaces.category.CategoryService;
import ru.practicum.ewm.mapper.Mapper;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCategoryController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminCategoryController.class);
    private static final String CATEGORY_PATH = "/categories";

    private final CategoryService categoryService;

    @PostMapping(CATEGORY_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {

        LOG.info("Поступил POST запрос в endPoint \"/admin/categories\" для создания категории: {}", categoryDto.getName());

        return Mapper.toCategoryDto(categoryService.addCategory(categoryDto));
    }

    @PatchMapping(CATEGORY_PATH + "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable(name = "catId") Long catId, @RequestBody String name) {

        LOG.info("""
                Получили PATCH запрос в endPoint \"/admin/categories/{catId}\" для обновления категории с ID {}
                Новая категория: {}
                """, catId, name);

        return Mapper.toCategoryDto(categoryService.updateCategory(catId, name));
    }

    @DeleteMapping(CATEGORY_PATH + "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable(name = "catId") Long catId) {

        LOG.info("Получили DELETE запрос в endPoint \"admin/categories/{catId}\" на удаление категории с ID {}", catId);

        categoryService.removeCategory(catId);
    }
}

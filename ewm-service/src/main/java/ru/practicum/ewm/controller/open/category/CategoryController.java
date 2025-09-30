package ru.practicum.ewm.controller.open.category;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.ewm.interfaces.category.CategoryService;
import ru.practicum.ewm.mapper.Mapper;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final static Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(name = "from") Integer from, @RequestParam(name = "size") Integer size) {

        LOG.info("Получили GET запрос в endPoint \"categories\"." +
                "Ограничение на вывод списка:\nКол-во объектов для пропуска: {}\nОграничение кол-ва: {}", from, size);

        return categoryService.getCategories(from, size)
                .stream()
                .map(Mapper::toCategoryDto)
                .toList();
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable(name = "catId") Long catId) {

        LOG.info("Получили GET запрос в endPoint \"/categories/{catId}\" для получения информации об категории с ID " + catId);

        return Mapper.toCategoryDto(categoryService.getCategory(catId));
    }
}

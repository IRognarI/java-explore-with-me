package ru.practicum.ewm.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.ewm.controller.admin.category.AdminCategoryController;
import ru.practicum.ewm.interfaces.category.CategoryService;
import ru.practicum.ewm.model.category.Category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCategoryController.class)
public class AdminCategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private Category category_1;

    private CategoryDto categoryDto_1;
    private CategoryDto categoryDto_2;
    private CategoryDto categoryDto_3;

    @BeforeEach
    public void setUp() {
        categoryDto_1 = CategoryDto.builder()
                .name("category#1")
                .build();

        categoryDto_2 = CategoryDto.builder()
                .name(null)
                .build();

        categoryDto_3 = CategoryDto.builder()
                .name("")
                .build();

        category_1 = Category.builder()
                .id(1L)
                .name(categoryDto_1.getName())
                .build();

        Mockito.lenient().when(categoryService.addCategory(Mockito.any(CategoryDto.class))).thenReturn(category_1);

    }

    @Test
    public void addCategory_Correct() throws Exception {

        mvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoryDto_1)))
                .andExpect(status().isCreated());
    }

    @Test
    public void addCategory_shouldThrowWhenNameIsnull() throws Exception {

        mvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoryDto_2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCategory_shouldThrowWhenNameIsBlank() throws Exception {

        mvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoryDto_3)))
                .andExpect(status().isBadRequest());
    }
}

package ru.practicum.ewm.category.controller.open;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.open.category.CategoryController;
import ru.practicum.ewm.interfaces.category.CategoryService;
import ru.practicum.ewm.model.category.Category;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryService categoryService;

    private List<Category> categoryList;

    @BeforeEach
    public void setUp() {
        categoryList = List.of(Category.builder().id(1L).name("name#1").build(),
                Category.builder().id(2L).name("name#2").build(),
                Category.builder().id(3L).name("name#3").build());
    }

    @Test
    public void getCategories_Correct() throws Exception {
        Mockito.when(categoryService.getCategories(Mockito.anyInt(), Mockito.anyInt())).thenReturn(categoryList);

        String from = "0";
        String size = "3";

        mvc.perform(get("/categories")
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk());
    }

    @Test
    public void getCategory_Correct() throws Exception {
        Mockito.when(categoryService.getCategory(Mockito.anyLong())).thenReturn(categoryList.get(0));

        mvc.perform(get("/categories/1"))
                .andExpect(status().isOk());
    }
}

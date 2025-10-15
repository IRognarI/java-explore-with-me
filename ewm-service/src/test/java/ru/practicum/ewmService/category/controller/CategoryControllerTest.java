package ru.practicum.ewmService.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.category.dto.NewCategoryDto;
import ru.practicum.ewmService.category.interfaces.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void addCategory() throws Exception {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Test Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        when(categoryService.addCategory(any(NewCategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategoryDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDto)));

        verify(categoryService).addCategory(newCategoryDto);
    }

    @Test
    void addCategory_whenInvalidRequest_thenBadRequest() throws Exception {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("");

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategoryDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory() throws Exception {
        long catId = 1L;
        NewCategoryDto updateCategoryDto = new NewCategoryDto();
        updateCategoryDto.setName("Updated Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(catId);
        categoryDto.setName("Updated Category");

        when(categoryService.updateCategory(anyLong(), any(NewCategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(patch("/admin/categories/{catId}", catId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDto)));

        verify(categoryService).updateCategory(catId, updateCategoryDto);
    }

    @Test
    void updateCategory_whenInvalidRequest_thenBadRequest() throws Exception {
        long catId = 1L;
        NewCategoryDto updateCategoryDto = new NewCategoryDto();
        updateCategoryDto.setName("");

        mockMvc.perform(patch("/admin/categories/{catId}", catId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCategoryDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategory() throws Exception {
        long catId = 1L;
        doNothing().when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(delete("/admin/categories/{catId}", catId))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(catId);
    }

    @Test
    void getCategories() throws Exception {
        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(1L);
        categoryDto1.setName("Category 1");

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("Category 2");

        List<CategoryDto> categories = List.of(categoryDto1, categoryDto2);

        when(categoryService.getCategories(anyInt(), anyInt())).thenReturn(categories);

        mockMvc.perform(get("/categories")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categories)));

        verify(categoryService).getCategories(0, 10);
    }

    @Test
    void getCategories_whenDefaultParams() throws Exception {
        when(categoryService.getCategories(anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(categoryService).getCategories(0, 10);
    }

    @Test
    void getCategory() throws Exception {
        long catId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(catId);
        categoryDto.setName("Test Category");

        when(categoryService.getCategory(anyLong())).thenReturn(categoryDto);

        mockMvc.perform(get("/categories/{catId}", catId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDto)));

        verify(categoryService).getCategory(catId);
    }
}
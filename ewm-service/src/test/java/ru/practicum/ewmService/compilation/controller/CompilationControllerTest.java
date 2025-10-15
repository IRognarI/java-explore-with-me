package ru.practicum.ewmService.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.compilation.dto.CompilationDto;
import ru.practicum.ewmService.compilation.dto.NewCompilationDto;
import ru.practicum.ewmService.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewmService.compilation.interfaces.CompilationService;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.user.dto.UserShortDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompilationController.class)
class CompilationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompilationService compilationService;

    @Test
    void addCompilation() throws Exception {

        NewCompilationDto newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle("Test Compilation");
        newCompilationDto.setPinned(true);
        newCompilationDto.setEvents(List.of(1L, 2L));

        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(1L);
        compilationDto.setTitle("Test Compilation");
        compilationDto.setPinned(true);
        compilationDto.setEvents(List.of(createEventShortDto(1L), createEventShortDto(2L)));

        when(compilationService.addCompilation(any(NewCompilationDto.class))).thenReturn(compilationDto);


        mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompilationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(compilationDto)));

        verify(compilationService).addCompilation(newCompilationDto);
    }

    @Test
    void updateCompilation() throws Exception {
        long compId = 1L;

        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest();
        updateRequest.setTitle("Updated Compilation");
        updateRequest.setPinned(false);

        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compId);
        compilationDto.setTitle("Updated Compilation");
        compilationDto.setPinned(false);
        compilationDto.setEvents(List.of());

        when(compilationService.updateCompilation(anyLong(), any(UpdateCompilationRequest.class))).thenReturn(compilationDto);

        mockMvc.perform(patch("/admin/compilations/{compId}", compId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(compilationDto)));

        verify(compilationService).updateCompilation(compId, updateRequest);
    }

    @Test
    void deleteCompilation() throws Exception {
        long compId = 1L;

        mockMvc.perform(delete("/admin/compilations/{compId}", compId))
                .andExpect(status().isNoContent());

        verify(compilationService).deleteCompilation(compId);
    }

    @Test
    void getCompilations() throws Exception {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(1L);
        compilationDto.setTitle("Test Compilation");
        compilationDto.setPinned(true);
        compilationDto.setEvents(List.of(createEventShortDto(1L)));

        List<CompilationDto> compilations = List.of(compilationDto);

        when(compilationService.getCompilations(anyBoolean(), anyInt(), anyInt())).thenReturn(compilations);

        mockMvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(compilations)));

        verify(compilationService).getCompilations(true, 0, 10);
    }

    @Test
    void getCompilation() throws Exception {
        long compId = 1L;

        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compId);
        compilationDto.setTitle("Test Compilation");
        compilationDto.setPinned(true);
        compilationDto.setEvents(List.of(createEventShortDto(1L)));

        when(compilationService.getCompilation(anyLong())).thenReturn(compilationDto);

        mockMvc.perform(get("/compilations/{compId}", compId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(compilationDto)));

        verify(compilationService).getCompilation(compId);
    }

    private EventShortDto createEventShortDto(long id) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Category");

        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(1L);
        userShortDto.setName("User");

        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(id);
        eventShortDto.setTitle("Event " + id);
        eventShortDto.setAnnotation("Annotation " + id);
        eventShortDto.setCategory(categoryDto);
        eventShortDto.setConfirmedRequests(0L);
        eventShortDto.setEventDate("2025-12-31 23:59:59");
        eventShortDto.setInitiator(userShortDto);
        eventShortDto.setPaid(false);
        eventShortDto.setViews(0L);

        return eventShortDto;
    }
}
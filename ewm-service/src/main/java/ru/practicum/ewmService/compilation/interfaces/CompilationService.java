package ru.practicum.ewmService.compilation.interfaces;

import ru.practicum.ewmService.compilation.dto.CompilationDto;
import ru.practicum.ewmService.compilation.dto.NewCompilationDto;
import ru.practicum.ewmService.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(long compId);

    CompilationDto addCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateRequest);

    void deleteCompilation(long compId);
}
package ru.practicum.ewmService.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmService.compilation.dto.CompilationDto;
import ru.practicum.ewmService.compilation.dto.NewCompilationDto;
import ru.practicum.ewmService.compilation.model.Compilation;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilation(NewCompilationDto dto) {
        return new Compilation(null, dto.getTitle(), dto.getPinned());
    }

    public CompilationDto toDto(Compilation compilation) {

        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());
        return dto;
    }
}
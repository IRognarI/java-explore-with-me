package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.userDto.UserDto;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.user.User;

@UtilityClass
public class Mapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category dtoToCategory(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }
}

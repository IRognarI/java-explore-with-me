package ru.practicum.ewmService.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект передачи данных (DTO) для представления комментария.
 * Этот класс инкапсулирует данные, связанные с комментарием, включая его ID, связанное событие,
 * комментатора, текстовое содержимое, рейтинг, статус и временную метку создания.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private long id;
    private long event;
    private long commenter;
    private String text;
    private Integer rate;
    private String status;
    private String created;
}
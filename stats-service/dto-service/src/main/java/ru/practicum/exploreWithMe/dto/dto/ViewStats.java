package ru.practicum.exploreWithMe.dto.dto;

/**
 * Представляет статистические данные для определенного URI приложения, включая количество просмотров.
 * Этот класс используется для инкапсуляции информации о статистике просмотров.
 */
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}

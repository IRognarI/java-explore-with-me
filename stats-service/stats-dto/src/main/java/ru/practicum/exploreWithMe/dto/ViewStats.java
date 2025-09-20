package ru.practicum.exploreWithMe.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Представляет статистические данные для определенного URI приложения, включая количество просмотров.
 * Этот класс используется для инкапсуляции информации о статистике просмотров.
 */
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(of = {"app", "hits"})
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}

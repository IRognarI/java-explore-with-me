package ru.practicum.exploreWithMe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Тестовый класс для проверки загрузки Spring контекста приложения.
 * Проверяет, что приложение может успешно стартовать и загружать все необходимые компоненты.
 */
@SpringBootTest
@ActiveProfiles("test")
class ExploreWithMeServerTest {

    /**
     * Тест проверяет, что Spring контекст загружается без ошибок.
     * Если контекст не может быть загружен (например, из-за ошибок конфигурации или отсутствия бинов),
     * тест завершится с ошибкой.
     */
    @Test
    void contextLoads() {
        // Этот тест будет провален, если Spring контекст не сможет загрузиться
        // Аннотация @SpringBootTest автоматически загружает полный контекст приложения
        // Если тест проходит успешно, это означает, что контекст приложения загрузился корректно
    }
}

package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи информации о хите (запросе).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    private Long id;          // readOnly, проставляет server
    private String app;       // название сервиса (например ewm-main-service)
    private String uri;       // URI (например /events/1)
    private String ip;        // IP клиента
    private String timestamp; // дата/время запроса ("yyyy-MM-dd HH:mm:ss")
}

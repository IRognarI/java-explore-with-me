package ru.practicum.ewmService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.statsClient.StatsClient;

/**
 * Создает и настраивает бин {@link StatsClient} для взаимодействия со службой статистики.
 * Клиент инициализируется URL-адресом по умолчанию, указывающим на локальную службу статистики.
 *
 * @return настроенный экземпляр {@link StatsClient}
 */
@Configuration
public class StatsClientConfig {

    @Value("${stats.server.url}")
    private String statsServerUrl;

    @Bean
    public StatsClient statsClient() {
        return new StatsClient(statsServerUrl);
    }
}
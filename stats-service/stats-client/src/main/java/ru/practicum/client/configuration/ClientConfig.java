package ru.practicum.client.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {

    @Value("${stats-server.url}")
    private String baseUrl;

    @Value("${stats-server.port}")
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public String pathForEndPoint(String path) {
        return String.format("%s:%d%s", baseUrl, port, path);
    }
}

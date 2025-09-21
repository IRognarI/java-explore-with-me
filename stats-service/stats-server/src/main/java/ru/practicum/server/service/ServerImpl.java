package ru.practicum.server.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.dto.requestDto.RequestDto;
import ru.practicum.server.exception.ErrorGettingAnIpAddress;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.interfaces.Server;
import ru.practicum.server.mapper.Mapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.JpaEndpointHit;

/**
 * Реализация интерфейса {@link Server} для сбора статистики.
 * Этот класс отвечает за обработку логики сбора статистических данных.
 * Использует {@link JpaEndpointHit} для взаимодействия с уровнем данных по статистике обращений к эндпоинтам.
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerImpl implements Server {
    private static final Logger LOG = LoggerFactory.getLogger(ServerImpl.class);
    private final JpaEndpointHit repository;

    @Override
    @Transactional
    public boolean addHit(RequestDto requestDto, HttpServletRequest request) {
        String error;
        if (requestDto == null) {
            error = "Не достаточно данных для статистики";
            LOG.error(error);
            throw new ValidationException(error);
        }

        LOG.info("""
                Получили запрос для сервиса: {}
                Путь: {}
                IP адрес: {}
                Дата и время: {}
                """, requestDto.getApp(), request.getRequestURI(), request.getLocalAddr(), requestDto.getTimestamp());

        String ip = request.getLocalAddr();
        if (ip == null) {
            error = "Ошибка получения IP адреса из " + request.getRequestURI();
            LOG.error(error);
            throw new ErrorGettingAnIpAddress(error);
        }

        requestDto.setIp(ip);

        EndpointHit endpointHit = repository.save(Mapper.toEntityFromRequestDto(requestDto));

        return endpointHit.getId() != null;
    }
}

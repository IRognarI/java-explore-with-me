package ru.practicum.statsServer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statsDto.NewHitDto;
import ru.practicum.statsDto.StatsItem;
import ru.practicum.statsDto.StatsItemDto;
import ru.practicum.statsServer.exceptions.IsBadRequestException;
import ru.practicum.statsServer.interfaces.HitService;
import ru.practicum.statsServer.mapper.Mapper;
import ru.practicum.statsServer.model.Hit;
import ru.practicum.statsServer.repository.JpaHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class HitServiceImpl implements HitService {

    private final JpaHitRepository hitRepository;

    @Override
    public void addHit(NewHitDto dto) {

        Hit hit = Mapper.toEntity(dto);
        hitRepository.save(hit);
        log.info("Added a new hit: {}", hit);
    }

    @Override
    public List<StatsItemDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (start.isAfter(end))
            throw new IsBadRequestException("Start date is after end date");

        List<StatsItem> viewStats;
        if (uris == null || uris.isEmpty()) {
            viewStats = unique ?
                    hitRepository.findAllUniqueHits(start, end) :
                    hitRepository.findAllNonUniqueHits(start, end);
        } else {
            viewStats = unique ?
                    hitRepository.findAllUniqueHitsInUris(start, end, uris) :
                    hitRepository.findAllNonUniqueHitsInUris(start, end, uris);
        }

        log.info("Found view stats items size={}", viewStats.size());
        return viewStats.stream()
                .map(StatsItemDto::new)
                .toList();
    }
}
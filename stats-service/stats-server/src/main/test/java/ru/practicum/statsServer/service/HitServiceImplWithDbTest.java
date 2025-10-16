package ru.practicum.statsServer.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsDto.NewHitDto;
import ru.practicum.statsDto.StatsItemDto;
import ru.practicum.statsServer.exceptions.IsBadRequestException;
import ru.practicum.statsServer.model.Hit;
import ru.practicum.statsServer.repository.JpaHitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class HitServiceImplWithDbTest {

    @Autowired
    private HitServiceImpl hitService;

    @Autowired
    private JpaHitRepository hitRepository;

    @Test
    void addHit_shouldSaveToDatabase() {
        NewHitDto dto = new NewHitDto();
        dto.setApp("testApp");
        dto.setUri("/test");
        dto.setIp("192.168.1.1");
        dto.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        hitService.addHit(dto);

        List<Hit> hits = hitRepository.findAll();
        assertEquals(1, hits.size());
        Hit hit = hits.get(0);
        assertEquals("testApp", hit.getApp());
        assertEquals("/test", hit.getUri());
        assertEquals("192.168.1.1", hit.getIp());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0, 0), hit.getTimestamp());
    }

    @Test
    void getStats_whenStartAfterEnd_shouldThrowException() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 31, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);
        List<String> uris = List.of("/test");

        assertThrows(IsBadRequestException.class, () -> {
            hitService.getStats(start, end, uris, true);
        });
    }

    @Test
    void getStats_withNonUniqueHits_shouldReturnCorrectStats() {
        Hit hit1 = new Hit();
        hit1.setApp("app1");
        hit1.setUri("/uri1");
        hit1.setIp("192.168.1.1");
        hit1.setTimestamp(LocalDateTime.of(2023, 6, 1, 10, 0, 0));

        Hit hit2 = new Hit();
        hit2.setApp("app1");
        hit2.setUri("/uri1");
        hit2.setIp("192.168.1.2");
        hit2.setTimestamp(LocalDateTime.of(2023, 6, 1, 11, 0, 0));

        Hit hit3 = new Hit();
        hit3.setApp("app2");
        hit3.setUri("/uri2");
        hit3.setIp("192.168.1.3");
        hit3.setTimestamp(LocalDateTime.of(2023, 6, 1, 12, 0, 0));

        hitRepository.save(hit1);
        hitRepository.save(hit2);
        hitRepository.save(hit3);

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);
        List<StatsItemDto> result = hitService.getStats(start, end, null, false);


        assertEquals(2, result.size());
        assertEquals("app1", result.get(0).getApp());
        assertEquals("/uri1", result.get(0).getUri());
        assertEquals(2, result.get(0).getHits());
        assertEquals("app2", result.get(1).getApp());
        assertEquals("/uri2", result.get(1).getUri());
        assertEquals(1, result.get(1).getHits());
    }

    @Test
    void getStats_withUniqueHits_shouldReturnCorrectStats() {
        Hit hit1 = new Hit();
        hit1.setApp("app1");
        hit1.setUri("/uri1");
        hit1.setIp("192.168.1.1");
        hit1.setTimestamp(LocalDateTime.of(2023, 6, 1, 10, 0, 0));

        Hit hit2 = new Hit();
        hit2.setApp("app1");
        hit2.setUri("/uri1");
        hit2.setIp("192.168.1.1");
        hit2.setTimestamp(LocalDateTime.of(2023, 6, 1, 11, 0, 0));

        Hit hit3 = new Hit();
        hit3.setApp("app1");
        hit3.setUri("/uri1");
        hit3.setIp("192.168.1.2");
        hit3.setTimestamp(LocalDateTime.of(2023, 6, 1, 12, 0, 0));

        hitRepository.save(hit1);
        hitRepository.save(hit2);
        hitRepository.save(hit3);

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);
        List<StatsItemDto> result = hitService.getStats(start, end, null, true);

        assertEquals(1, result.size());
        assertEquals("app1", result.get(0).getApp());
        assertEquals("/uri1", result.get(0).getUri());
        assertEquals(2, result.get(0).getHits());
    }

    @Test
    void getStats_withUrisFilter_shouldReturnFilteredStats() {
        Hit hit1 = new Hit();
        hit1.setApp("app1");
        hit1.setUri("/uri1");
        hit1.setIp("192.168.1.1");
        hit1.setTimestamp(LocalDateTime.of(2023, 6, 1, 10, 0, 0));

        Hit hit2 = new Hit();
        hit2.setApp("app1");
        hit2.setUri("/uri2");
        hit2.setIp("192.168.1.2");
        hit2.setTimestamp(LocalDateTime.of(2023, 6, 1, 11, 0, 0));

        hitRepository.save(hit1);
        hitRepository.save(hit2);

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);
        List<String> uris = List.of("/uri1");
        List<StatsItemDto> result = hitService.getStats(start, end, uris, false);

        assertEquals(1, result.size());
        assertEquals("app1", result.get(0).getApp());
        assertEquals("/uri1", result.get(0).getUri());
        assertEquals(1, result.get(0).getHits());
    }
}
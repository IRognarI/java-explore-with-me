package ru.practicum.statsServer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.statsDto.NewHitDto;
import ru.practicum.statsDto.StatsItem;
import ru.practicum.statsDto.StatsItemDto;
import ru.practicum.statsServer.exceptions.IsBadRequestException;
import ru.practicum.statsServer.model.Hit;
import ru.practicum.statsServer.repository.JpaHitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HitServiceImplTest {

    @Mock
    private JpaHitRepository hitRepository;

    @InjectMocks
    private HitServiceImpl hitService;

    @Test
    void addHit_shouldSaveHit() {
        NewHitDto dto = new NewHitDto();
        dto.setApp("testApp");
        dto.setUri("/test");
        dto.setIp("192.168.1.1");
        dto.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        hitService.addHit(dto);

        verify(hitRepository, times(1)).save(org.mockito.ArgumentMatchers.any(Hit.class));
    }

    @Test
    void getStats_whenStartAfterEnd_shouldThrowIsBadRequestException() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 31, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);
        List<String> uris = List.of("/test");

        IsBadRequestException exception = assertThrows(
                IsBadRequestException.class,
                () -> hitService.getStats(start, end, uris, true)
        );

        assertEquals("Start date is after end date", exception.getMessage());
    }

    @Test
    void getStats_whenUrisIsNullAndUniqueTrue_shouldCallFindAllUniqueHits() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);

        StatsItem statsItem = new StatsItem() {
            @Override
            public String getApp() {
                return "testApp";
            }

            @Override
            public String getUri() {
                return "/test";
            }

            @Override
            public long getHits() {
                return 5L;
            }
        };

        List<StatsItem> statsItems = List.of(statsItem);

        when(hitRepository.findAllUniqueHits(start, end)).thenReturn(statsItems);

        List<StatsItemDto> result = hitService.getStats(start, end, null, true);

        assertEquals(1, result.size());
        assertEquals("testApp", result.get(0).getApp());
        assertEquals("/test", result.get(0).getUri());
        assertEquals(5L, result.get(0).getHits());
        verify(hitRepository, times(1)).findAllUniqueHits(start, end);
    }

    @Test
    void getStats_whenUrisIsEmptyAndUniqueFalse_shouldCallFindAllNonUniqueHits() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);
        List<String> uris = List.of();

        StatsItem statsItem = new StatsItem() {
            @Override
            public String getApp() {
                return "testApp";
            }

            @Override
            public String getUri() {
                return "/test";
            }

            @Override
            public long getHits() {
                return 3L;
            }
        };

        List<StatsItem> statsItems = List.of(statsItem);

        when(hitRepository.findAllNonUniqueHits(start, end)).thenReturn(statsItems);

        List<StatsItemDto> result = hitService.getStats(start, end, uris, false);

        assertEquals(1, result.size());
        assertEquals("testApp", result.get(0).getApp());
        assertEquals("/test", result.get(0).getUri());
        assertEquals(3L, result.get(0).getHits());
        verify(hitRepository, times(1)).findAllNonUniqueHits(start, end);
    }

    @Test
    void getStats_whenUrisProvidedAndUniqueTrue_shouldCallFindAllUniqueHitsInUris() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);
        List<String> uris = List.of("/test");

        StatsItem statsItem = new StatsItem() {
            @Override
            public String getApp() {
                return "testApp";
            }

            @Override
            public String getUri() {
                return "/test";
            }

            @Override
            public long getHits() {
                return 7L;
            }
        };

        List<StatsItem> statsItems = List.of(statsItem);

        when(hitRepository.findAllUniqueHitsInUris(start, end, uris)).thenReturn(statsItems);

        List<StatsItemDto> result = hitService.getStats(start, end, uris, true);

        assertEquals(1, result.size());
        assertEquals("testApp", result.get(0).getApp());
        assertEquals("/test", result.get(0).getUri());
        assertEquals(7L, result.get(0).getHits());
        verify(hitRepository, times(1)).findAllUniqueHitsInUris(start, end, uris);
    }

    @Test
    void getStats_whenUrisProvidedAndUniqueFalse_shouldCallFindAllNonUniqueHitsInUris() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);
        List<String> uris = List.of("/test");

        StatsItem statsItem = new StatsItem() {
            @Override
            public String getApp() {
                return "testApp";
            }

            @Override
            public String getUri() {
                return "/test";
            }

            @Override
            public long getHits() {
                return 10L;
            }
        };

        List<StatsItem> statsItems = List.of(statsItem);

        when(hitRepository.findAllNonUniqueHitsInUris(start, end, uris)).thenReturn(statsItems);

        List<StatsItemDto> result = hitService.getStats(start, end, uris, false);

        assertEquals(1, result.size());
        assertEquals("testApp", result.get(0).getApp());
        assertEquals("/test", result.get(0).getUri());
        assertEquals(10L, result.get(0).getHits());
        verify(hitRepository, times(1)).findAllNonUniqueHitsInUris(start, end, uris);
    }
}
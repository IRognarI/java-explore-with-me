package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.server.dto.requestDto.ViewStats;
import ru.practicum.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaEndpointHit extends CrudRepository<EndpointHit, Long> {

    @Query(value = """
            select e.app, e.uri,
                   case when :unique = true
                        then count(distinct e.ip)
                        else count(e.ip)
            end as hits
            from endpoint_hits as e
            where e.timestamp between :start and :end
            and uri in (:uris)
            group by e.app, e.uri
            """, nativeQuery = true)
    List<ViewStats> getStatsWithHist(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris,
            @Param("unique") Boolean unique
    );
}

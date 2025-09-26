package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.responseDto.ViewStats;
import ru.practicum.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaEndpointHit extends CrudRepository<EndpointHit, Long> {
    @Query("""
    select new ru.practicum.dto.responseDto.ViewStats(
        e.app,
        e.uri,
        case when :unique = true then count(distinct e.ip) else count(e.ip) end
    )
    from EndpointHit e
    where e.timestamp between :start and :end
      and (:uris is null or e.uri in :uris)
    group by e.app, e.uri
    order by case when :unique = true then count(distinct e.ip) else count(e.ip) end desc
    """)
    List<ViewStats> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") String[] uris,
            @Param("unique") Boolean unique);
}
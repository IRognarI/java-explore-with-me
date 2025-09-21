package ru.practicum.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.EndpointHit;

@Repository
public interface JpaEndpointHit extends CrudRepository<EndpointHit, Long> {
}

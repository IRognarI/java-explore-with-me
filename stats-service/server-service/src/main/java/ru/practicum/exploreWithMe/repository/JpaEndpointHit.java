package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.exploreWithMe.model.EndpointHit;

@Repository
public interface JpaEndpointHit extends JpaRepository<EndpointHit, Long> {
}

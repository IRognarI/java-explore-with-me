package ru.practicum.ewmService.user.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmService.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    List<User> findAllByIdInOrderByIdAsc(List<Long> ids, PageRequest pageRequest);

    @Query("""
            select u from User u order by u.id
            """)
    List<User> findAllOrderByIdAsc(PageRequest pageRequest);
}
package ru.practicum.ewmService.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmService.comment.model.Comment;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.user.model.User;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {

    @Query("""
            select avg(c.rate)
            from comments c
            where c.event = :event
            """)
    Double getAverageRate(Event event);

    boolean existsByEventAndCommenter(Event event, User user);

    Page<Comment> findAllByEventAndRateIsNotNullOrderByCreatedAsc(Event event, Pageable pageable);

    Page<Comment> findAllByEventOrderByCreatedAsc(Event event, Pageable pageable);

    Page<Comment> findAllByCommenterOrderByCreatedAsc(User user, Pageable pageable);

    Page<Comment> findAllByCommenterAndRateIsNotNullOrderByCreatedAsc(User user, Pageable pageable);

    boolean existsByCommenterId(Long commenterId);
}
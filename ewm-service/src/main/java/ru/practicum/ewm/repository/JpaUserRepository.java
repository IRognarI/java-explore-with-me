package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.user.User;

import java.util.List;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    @Query(value = """
            select *
            from users as u
            where u.user_id in :ids
            limit :size
            offset :from
            """, nativeQuery = true)
    List<User> getUserByParam(
            @Param("ids")
            Long[] ids,
            @Param("from")
            Integer from,
            @Param("size")
            Integer size);

    User getUserById(Long id);

    User getUserByEmail(String email);
}

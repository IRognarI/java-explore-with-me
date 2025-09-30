package ru.practicum.ewm.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.category.Category;

import java.util.List;

@Repository
public interface JpaCategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = """
            select case
            when exists (
                 select 1
                 from categories
                 where lower(category_name) = :name
                         )
            then 'true'
            else 'false'
            end
            """, nativeQuery = true)
    boolean categoryExists(String name);

    Category getCategoryById(Long id);

    Category getCategoryByName(String name);

    @Query(value = """
            select *
            from categories as c
            limit :size
            offset :from
            """, nativeQuery = true)
    List<Category> getCategories(@Param("from") int from, @Param("size") int size);

}

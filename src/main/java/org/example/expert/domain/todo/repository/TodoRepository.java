package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryQueryDSL{

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @EntityGraph
    @Query("SELECT t FROM Todo t " +
            "WHERE (:weather is null or t.weather= :weather) " +
            "AND (:startAt is null or t.modifiedAt >= :startAt) " +
            "AND (:endAt is null or t.modifiedAt <= :endAt) " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findtodobyweather(
            @Param("weather") String weather,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            Pageable pageable
    );
}

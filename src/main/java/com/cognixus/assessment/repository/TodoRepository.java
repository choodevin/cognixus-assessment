package com.cognixus.assessment.repository;

import com.cognixus.assessment.model.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {
    Optional<Todo> findByDscp(String dscp);

    Optional<Todo> findByDscpAndUserId(String title, UUID userid);

    Optional<Todo> findByUserId(UUID userId);
}

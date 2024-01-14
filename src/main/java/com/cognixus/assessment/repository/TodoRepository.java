package com.cognixus.assessment.repository;

import com.cognixus.assessment.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

    Optional<Todo> findByTitleAndUserId(String title, UUID userid);

    Optional<Todo> findByUserId(UUID userId);
}

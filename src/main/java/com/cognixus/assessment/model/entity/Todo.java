package com.cognixus.assessment.model.entity;

import com.cognixus.assessment.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "todo")
public class Todo {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private Status status;

    @Column(name = "user_id")
    private UUID userId;
}

package com.cognixus.assessment.model.entity;

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

    @Column(name = "dscp")
    private String dscp;

    @Column(name = "isdone")
    private boolean isDone;

    @Column(name = "user_id")
    private UUID userId;
}

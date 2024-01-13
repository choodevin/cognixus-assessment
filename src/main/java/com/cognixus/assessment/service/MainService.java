package com.cognixus.assessment.service;

import com.cognixus.assessment.model.entity.Todo;
import com.cognixus.assessment.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MainService {
    @Autowired
    private TodoRepository todoRepository;

    public ResponseEntity<String> getTodoListById(UUID id) {
        Optional<Todo> todoOptional = todoRepository.findById(id);

        return todoOptional.map(value -> new ResponseEntity<>(value.getDscp(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>("No Record(s) Found", HttpStatus.OK));
    }
}

package com.cognixus.assessment.controller;

import com.cognixus.assessment.constants.ResourcePath;
import com.cognixus.assessment.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ResourcePath.DEFAULT_ENTRY)
public class MainController {
    @Autowired
    private MainService mainService;

    @GetMapping(value = ResourcePath.GET_TODO_LIST_BY_ID)
    public ResponseEntity<String> getTodoListById(@PathVariable("id") String id) {
        return mainService.getTodoListById(UUID.fromString(id));
    }
}

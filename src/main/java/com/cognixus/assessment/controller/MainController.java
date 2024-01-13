package com.cognixus.assessment.controller;

import com.cognixus.assessment.constants.ResourcePath;
import com.cognixus.assessment.enums.Action;
import com.cognixus.assessment.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ResourcePath.DEFAULT_ENTRY)
public class MainController {
    @Autowired
    private MainService mainService;

    @GetMapping(value = ResourcePath.GET_ALL_TODO)
    public ResponseEntity<?> getAllTodo(@RequestHeader("token") String token) {
        return mainService.getAllTodo(token);
    }

    @PostMapping(value = ResourcePath.ADD_TODO_LIST)
    public ResponseEntity<String> addTodoList(@RequestBody String title, @RequestHeader("token") String token) {
        return mainService.addTodoList(title, token);
    }

    @PostMapping(value = ResourcePath.ACTION_TODO_LIST)
    public ResponseEntity<String> todoListAction(@RequestBody String title, @PathVariable("action") Action action, @RequestHeader("token") String token) {
        return mainService.todoListAction(title, action, token);
    }
}

package com.cognixus.assessment.service;

import com.cognixus.assessment.enums.Action;
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
    @Autowired
    private LoginService loginService;

    public ResponseEntity<?> getAllTodo(String token) {
        try {
            UUID userId = loginService.getUserId(token);
            Optional<Todo> todoOptional = todoRepository.findByUserId(userId);

            return todoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    public ResponseEntity<String> addTodoList(String title, String token) {
        try {
            UUID userId = loginService.getUserId(token);
            Optional<Todo> todoOptional = todoRepository.findByDscpAndUserId(title, userId);

            if (todoOptional.isPresent()) {
                throw new Exception("Duplicate title found.");
            }

            Todo todo = new Todo();
            todo.setId(UUID.randomUUID());
            todo.setDone(false);
            todo.setDscp(title);
            todo.setUserId(userId);

            todoRepository.saveAndFlush(todo);

            return ResponseEntity.ok("Todo has been added. Use API /set/{action} to update todo item's status.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    public ResponseEntity<String> todoListAction(String title, Action action, String token) {
        try {
            UUID userId = loginService.getUserId(token);
            Optional<Todo> todoOptional = todoRepository.findByDscpAndUserId(title, userId);

            if (todoOptional.isPresent()) {
                Todo todo = todoOptional.get();

                if (action.equals(Action.DONE) || action.equals(Action.UNDONE)) {
                    todo.setDone(action.equals(Action.DONE));
                    todoRepository.saveAndFlush(todo);
                } else if (action.equals(Action.DELETE)) {
                    todoRepository.delete(todo);
                }

                return ResponseEntity.ok((action.equals(Action.DELETE) ?
                        "Todo item has been deleted." : "Todo status has been updated.")
                        + " Use API /get-todo-list to view all todo items.");
            } else {
                throw new Exception("Todo item not found!");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}

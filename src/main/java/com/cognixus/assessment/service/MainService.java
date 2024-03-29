package com.cognixus.assessment.service;

import com.cognixus.assessment.enums.Action;
import com.cognixus.assessment.enums.Status;
import com.cognixus.assessment.model.Todo;
import com.cognixus.assessment.repository.TodoRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MainService {
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private LoginService loginService;

    public ResponseEntity<?> getAllTodo(String token) {
        try {
            UUID userId = loginService.getUserId(token);
            List<Todo> todoList = todoRepository.findByUserId(userId);

            if (todoList == null || todoList.isEmpty()) {
                throw new Exception("No Record(s) found!");
            }

            return ResponseEntity.ok(todoList);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    public ResponseEntity<String> addTodoList(String title, String token) {
        try {
            UUID userId = loginService.getUserId(token);
            Optional<Todo> todoOptional = todoRepository.findByTitleAndUserId(title, userId);

            if (todoOptional.isPresent()) {
                throw new Exception("Duplicate title found.");
            }

            Todo todo = new Todo();
            todo.setId(UUID.randomUUID());
            todo.setStatus(Status.NOT_DONE);
            todo.setTitle(title);
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
            Optional<Todo> todoOptional = todoRepository.findByTitleAndUserId(title, userId);

            if (todoOptional.isPresent()) {
                Todo todo = todoOptional.get();

                if (action.equals(Action.DONE) || action.equals(Action.UNDONE)) {
                    todo.setStatus(action.equals(Action.DONE) ? Status.DONE : Status.NOT_DONE);
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

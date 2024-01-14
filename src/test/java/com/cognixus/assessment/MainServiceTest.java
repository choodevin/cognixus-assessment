package com.cognixus.assessment;

import com.cognixus.assessment.enums.Action;
import com.cognixus.assessment.model.Todo;
import com.cognixus.assessment.repository.TodoRepository;
import com.cognixus.assessment.service.LoginService;
import com.cognixus.assessment.service.MainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class MainServiceTest {
    @Mock
    private MainService mainService;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private LoginService loginService;
    private String token;
    private String title;
    private Todo todo;

    @BeforeEach
    public void setup() {
        mainService = new MainService(todoRepository, loginService);
        token = "sample_token";
        title = "sample_title";
        todo = new Todo();
    }

    @Test
    public void testAction_Success() throws Exception {
        when(loginService.getUserId(anyString())).thenReturn(UUID.randomUUID());
        when(todoRepository.findByTitleAndUserId(anyString(), any(UUID.class))).thenReturn(Optional.of(todo));
        assertEquals(ResponseEntity.ok("Todo status has been updated. Use API /get-todo-list to view all todo items."), mainService.todoListAction(title, Action.DONE, token));
        assertEquals(ResponseEntity.ok("Todo status has been updated. Use API /get-todo-list to view all todo items."), mainService.todoListAction(title, Action.UNDONE, token));
        assertEquals(ResponseEntity.ok("Todo item has been deleted. Use API /get-todo-list to view all todo items."), mainService.todoListAction(title, Action.DELETE, token));
    }

    @Test
    public void testAction_NotFound() throws Exception {
        when(loginService.getUserId(anyString())).thenReturn(UUID.randomUUID());
        when(todoRepository.findByTitleAndUserId(anyString(), any(UUID.class))).thenReturn(Optional.empty());
        assertEquals(ResponseEntity.internalServerError().body("Todo item not found!"), mainService.todoListAction(title, Action.DONE, token));
    }

    @Test
    public void testAction_Exception() throws Exception {
        when(loginService.getUserId(anyString())).thenThrow(new Exception("User not found or token expired"));
        assertEquals(ResponseEntity.internalServerError().body("User not found or token expired"), mainService.todoListAction(title, Action.DONE, token));
    }
}

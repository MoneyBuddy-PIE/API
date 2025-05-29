package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.TaskRequest;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.service.TaskService;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService service;

    @PostMapping("")
    public ResponseEntity<AuthResponse> createTask(
        @Valid @RequestBody TaskRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        return service.createTask(request, token);
    }

    @GetMapping("")
    public ResponseEntity<List<Task>> getTasks(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false) String source
    ) {
        String token = authHeader.substring(7);
        return service.getTasks(token, source);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(
        @PathVariable String id
    ) {
        return service.getTask(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AuthResponse> deleteTask(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String id
    ) {
        String token = authHeader.substring(7);
        return service.deleteTask(token, id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> modifyTask(
        @Valid @RequestBody TaskRequest request,
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String id
    ) {
        String token = authHeader.substring(7);
        return service.modifyTask(request, token, id);
    }

    @PutMapping("/complete/{id}")
    public ResponseEntity<AuthResponse> completeTask(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String id
    ) {
        String token = authHeader.substring(7);
        return service.completeTask(token, id);
    }
}

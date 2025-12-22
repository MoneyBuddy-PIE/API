/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.TaskComplete;
import moneybuddy.fr.moneybuddy.dtos.TaskRequest;
import moneybuddy.fr.moneybuddy.dtos.TaskUpdate;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.TaskHistory;
import moneybuddy.fr.moneybuddy.model.TaskWithHistory;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;
import moneybuddy.fr.moneybuddy.service.TaskHistoryService;
import moneybuddy.fr.moneybuddy.service.TaskService;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService service;
  private final TaskHistoryService taskHistoryService;

  @PostMapping("")
  public ResponseEntity<AuthResponse> createTask(
      @Valid @RequestBody TaskRequest request, @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    return service.createTask(request, token);
  }

  @GetMapping("")
  public ResponseEntity<List<Task>> getTasks(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) String childId,
      @RequestParam(required = false) TaskStatus status,
      @RequestParam(required = false) TaskType type) {
    String token = authHeader.substring(7);
    return service.getTasks(token, childId, status, type);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskWithHistory> getTask(@PathVariable String id) {
    return service.getTask(id);
  }

  @GetMapping("/history/{id}")
  public ResponseEntity<List<TaskHistory>> getTaskHistory(@PathVariable String id) {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskHistoryService.getTaskHistory(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<AuthResponse> deleteTask(
      @RequestHeader("Authorization") String authHeader, @PathVariable String id) {
    String token = authHeader.substring(7);
    return service.deleteTask(token, id);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Task> modifyTask(
      @Valid @RequestBody TaskUpdate request,
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String id) {
    String token = authHeader.substring(7);
    return service.modifyTask(request, token, id);
  }

  @PutMapping("/complete/{id}")
  public ResponseEntity<AuthResponse> completeTask(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String id,
      @Valid @RequestBody TaskComplete req) {
    String token = authHeader.substring(7);
    return service.completeTask(req, token, id);
  }

  @PutMapping("/prevalidation/{id}")
  public ResponseEntity<AuthResponse> preValidationTask(
      @RequestHeader("Authorization") String authHeader, @PathVariable String id) {
    String token = authHeader.substring(7);
    return service.preValidateTask(token, id);
  }
}

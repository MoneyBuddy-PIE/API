/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.CreateGoalRequest;
import moneybuddy.fr.moneybuddy.dtos.GoalMoneyRequest;
import moneybuddy.fr.moneybuddy.dtos.GoalRequest;
import moneybuddy.fr.moneybuddy.model.Goal;
import moneybuddy.fr.moneybuddy.model.enums.GoalStatus;
import moneybuddy.fr.moneybuddy.service.GoalService;
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
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

  private final GoalService service;

  @PostMapping("")
  public ResponseEntity<AuthResponse> createGoal(
      @Valid @RequestBody CreateGoalRequest request,
      @RequestHeader("Authorization") String authHeader) {

    String token = authHeader.substring(7);
    return service.createGoal(request, token);
  }

  @GetMapping("")
  public ResponseEntity<List<Goal>> getGoals(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) String childId,
      @RequestParam(required = false) String accountId,
      @RequestParam(required = false) GoalStatus goalStatus,
      @RequestParam(required = false) Number goalProgression) {
    String token = authHeader.substring(7);
    return service.getGoals(token, childId, goalStatus, goalProgression, accountId);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Goal> getGoal(@PathVariable String id) {
    return service.getGoal(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteGoal(
      @RequestHeader("Authorization") String authHeader, @PathVariable String id) {
    String token = authHeader.substring(7);
    return service.deleteGoal(token, id);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Goal> modifyGoal(
      @Valid @RequestBody GoalRequest request,
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String id) {
    String token = authHeader.substring(7);
    return service.modifyGoal(request, token, id);
  }

  @PostMapping("/add/{id}")
  public ResponseEntity<String> addGoalMoney(
      @Valid @RequestBody GoalMoneyRequest request,
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String id) {
    String token = authHeader.substring(7);
    return service.addGoalMoney(request, token, id);
  }

  @PostMapping("/remove/{id}")
  public ResponseEntity<String> removeGoalMoney(
      @Valid @RequestBody GoalMoneyRequest request,
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String id) {
    String token = authHeader.substring(7);
    return service.removeGoalMoney(request, token, id);
  }

  @PostMapping("/transfer/{id}")
  public ResponseEntity<String> transferGoalMoney(
      @RequestHeader("Authorization") String authHeader, @PathVariable String id) {
    String token = authHeader.substring(7);
    return service.confirmSavingMoneyTransfer(token, id);
  }
}

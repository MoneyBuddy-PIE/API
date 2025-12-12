/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.userProgress.UserProgressMakeCourseAsComplete;
import moneybuddy.fr.moneybuddy.service.UserProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class UserProgressController {
  private final UserProgressService userProgressService;

  @PostMapping("/complete-course")
  public ResponseEntity<Void> completeCourse(
      @Valid @RequestBody UserProgressMakeCourseAsComplete req,
      @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    userProgressService.markCourseAsCompleted(token, req);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }
}
